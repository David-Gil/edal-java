/*******************************************************************************
 * Copyright (c) 2014 The University of Reading
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Reading, nor the names of the
 *    authors or contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package uk.ac.rdg.resc.edal.dataset.cdm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ucar.ma2.ArrayDouble;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import uk.ac.rdg.resc.edal.cdm.CreateNetCDF;
import uk.ac.rdg.resc.edal.dataset.DataSource;
import uk.ac.rdg.resc.edal.dataset.GriddedDataset;
import uk.ac.rdg.resc.edal.dataset.HorizontallyDiscreteDataset;
import uk.ac.rdg.resc.edal.domain.HorizontalDomain;
import uk.ac.rdg.resc.edal.domain.MapDomain;
import uk.ac.rdg.resc.edal.exceptions.DataReadingException;
import uk.ac.rdg.resc.edal.exceptions.EdalException;
import uk.ac.rdg.resc.edal.exceptions.VariableNotFoundException;
import uk.ac.rdg.resc.edal.feature.DiscreteFeature;
import uk.ac.rdg.resc.edal.feature.GridFeature;
import uk.ac.rdg.resc.edal.feature.MapFeature;
import uk.ac.rdg.resc.edal.geometry.BoundingBox;
import uk.ac.rdg.resc.edal.grid.RegularGridImpl;
import uk.ac.rdg.resc.edal.position.HorizontalPosition;
import uk.ac.rdg.resc.edal.util.Array2D;
import uk.ac.rdg.resc.edal.util.CurvilinearCoords;
import uk.ac.rdg.resc.edal.util.CurvilinearCoords.Cell;
import uk.ac.rdg.resc.edal.util.ValuesArray2D;

/**
 * These tests perform various operations on a test curvilinear dataset
 * (originally generated from {@link CreateNetCDF}). These involve reading data
 * from the NetCDF file and extracting features from the dataset. Then checking
 * that the extracted values are those expected.
 * 
 * @author Nan Lin
 */
public class CurvilinearGridDatasetTest {
    // accuracy value set for assert equal method for comparison.
    private static final double delta = 1e-5;
    private HorizontallyDiscreteDataset<? extends DataSource> dataset;
    private NetcdfFile cdf;
    private String location;
    // parameters about the used test dataset
    private int etaSize = 336;
    private int xiSize = 896;

    /**
     * Read data in and create a curvilinear dataset object.
     * 
     * @throws IOException
     *             If there is a problem when open the file or create the
     *             dataset.
     * @throws EdalException
     *             If there is a problem when create the dataset.
     */
    @Before
    public void setUp() throws IOException, EdalException {
        URL url = this.getClass().getResource("/output-curvilinear.nc");
        location = url.getPath();
        cdf = NetcdfFile.open(location);
        CdmGridDatasetFactory datasetFactory = new CdmGridDatasetFactory();
        dataset = datasetFactory.createDataset("testdataset", location);
    }

    /**
     * Dateset contains only x and y data on the plane. When we try to extract
     * data including T and Z info, the method should return
     * UnupportedOperationException.
     * 
     * @throws DataReadingException
     *             if there is a problem reading data.
     * @throws VariableNotFoundException 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testUnSupportedOperation() throws DataReadingException, VariableNotFoundException {
        assertFalse(dataset.supportsProfileFeatureExtraction("allx_u"));
        assertFalse(dataset.supportsTimeseriesExtraction("allx_u"));

        HorizontalDomain hDomain = dataset.getVariableMetadata("allx_u").getHorizontalDomain();

        BoundingBox bbox = hDomain.getBoundingBox();

        Exception caughtEx = null;
        // the statement below catches UnsupportedOperationException.
        try {
            dataset.extractTimeseriesFeatures(null, bbox, null, null, null, null);
        } catch (UnsupportedOperationException e) {
            caughtEx = e;
        }
        assertNotNull(caughtEx);
        // the statement below throws UnsupportedOperationException.
        dataset.extractProfileFeatures(null, bbox, null, null, null, null);
    }

    /**
     * Test {@link CurvilinearCoords}.
     * 
     * @throws IOException
     *             if there is a problem when read data in the netCDf file
     * @throws VariableNotFoundException 
     * */

    @Test
    public void testCurviLinearCoords() throws IOException, VariableNotFoundException {
        Variable varLonRho = cdf.findVariable("lon_rho");
        Variable varLatRho = cdf.findVariable("lat_rho");

        ArrayDouble longitudeData = (ArrayDouble) varLonRho.read();
        ArrayDouble latitudeData = (ArrayDouble) varLatRho.read();

        ValuesArray2D longitudeValuesInEDALFormat = new ValuesArray2D(xiSize, etaSize);
        ValuesArray2D latitudeValuesInEDALFormat = new ValuesArray2D(xiSize, etaSize);
        for (int i = 0; i < longitudeData.getSize(); i++) {
            int m = i % etaSize;
            int n = i / etaSize;
            int[] coords = new int[] { n, m };
            longitudeValuesInEDALFormat.set(longitudeData.getDouble(i), coords);
            latitudeValuesInEDALFormat.set(latitudeData.getDouble(i), coords);
        }

        CurvilinearCoords cCoords = new CurvilinearCoords(longitudeValuesInEDALFormat,
                latitudeValuesInEDALFormat);

        BoundingBox expectedBbox = dataset.getVariableMetadata("allx_u").getHorizontalDomain()
                .getBoundingBox();

        assertEquals(expectedBbox, cCoords.getBoundingBox());
        assertEquals(etaSize, cCoords.getNi());
        assertEquals(xiSize, cCoords.getNj());

        // pick up a horizontal position with id=15000

        int index = 15000;
        HorizontalPosition expectedPos = new HorizontalPosition(longitudeData.getDouble(index),
                latitudeData.getDouble(index));
        int cCoords_i = index % etaSize;
        int cCoords_j = index / etaSize;

        /*
         * CurvilinearCoords use "float" to save coordinates but LatLonPosition
         * use double. This makes a little difficulty for testing. We have to
         * compare them side by side.
         */

        assertEquals(expectedPos.getX(), cCoords.getMidpoint(cCoords_i, cCoords_j).getX(), delta);
        assertEquals(expectedPos.getY(), cCoords.getMidpoint(cCoords_i, cCoords_j).getY(), delta);
        assertEquals(expectedPos.getCoordinateReferenceSystem(),
                cCoords.getMidpoint(cCoords_i, cCoords_j).getCoordinateReferenceSystem());

        List<Cell> cellList = cCoords.getCells();
        Cell cell = cCoords.getCell(cCoords_i, cCoords_j);
        assertEquals(cellList.get(index), cell);

        assertEquals(cCoords_i, cell.getI());
        assertEquals(cCoords_j, cell.getJ());
        assertEquals(expectedPos.getX(), cell.getCentre().getX(), delta);
        assertEquals(expectedPos.getY(), cell.getCentre().getY(), delta);
        assertEquals(expectedPos.getCoordinateReferenceSystem(), cell.getCentre()
                .getCoordinateReferenceSystem());
    }

    /**
     * Test the methods implemented in the abstract class
     * {@link GriddedDataset}.
     * 
     * @throws DataReadingException
     *             If there is a problem when reading the data
     * @throws VariableNotFoundException 
     * */
    @Test
    public void testCurviLinearDataset() throws DataReadingException, VariableNotFoundException {
        assertTrue(dataset instanceof GriddedDataset);

        List<Variable> variables = cdf.getVariables();
        Set<String> vars = new HashSet<>();
        for (Variable v : variables) {
            vars.add(v.getFullName());
        }
        /*
         * netCDF use variable but dataset use feature. Two different concepts.
         * How can I use another to get feature info?
         */

        // assertEquals(vars, dataset.getFeatureIds());
        GridFeature allxUValues = ((GriddedDataset) dataset).readFeature("allx_u");
        GridFeature allxVValues = ((GriddedDataset) dataset).readFeature("allx_v");
        GridFeature allyUValues = ((GriddedDataset) dataset).readFeature("ally_u");
        GridFeature allyVValues = ((GriddedDataset) dataset).readFeature("ally_v");

        for (int n = 0; n < xiSize; n++) {
            for (int m = 0; m < etaSize; m++) {
                float xUVale = allxUValues.getValues("allx_u").get(0, 0, m, n).floatValue();
                float xVVale = allxVValues.getValues("allx_v").get(0, 0, m, n).floatValue();
                float yUVale = allyUValues.getValues("ally_u").get(0, 0, m, n).floatValue();
                float yVVale = allyVValues.getValues("ally_v").get(0, 0, m, n).floatValue();

                // below four values are set by test dataset
                float expectedXU = m;
                float expectedXV = 0.0f;
                float expectedYU = 0.0f;
                float expectedYV = n;

                assertEquals(expectedXU, xUVale, delta);
                assertEquals(expectedXV, xVVale, delta);
                assertEquals(expectedYU, yUVale, delta);
                assertEquals(expectedYV, yVVale, delta);
            }
        }

        HorizontalDomain hDomain = dataset.getVariableMetadata("allx_u").getHorizontalDomain();

        BoundingBox bbox = hDomain.getBoundingBox();
        Collection<? extends DiscreteFeature<?, ?>> mapFeature = dataset.extractMapFeatures(null,
                new MapDomain(new RegularGridImpl(bbox, etaSize, xiSize), null, null));
        DiscreteFeature<?, ?> feature = mapFeature.iterator().next();
        MapFeature data = (MapFeature) feature;

        Array2D<Number> xUValues = data.getValues("allx_u");
        assertArrayEquals(new int[] { xiSize, etaSize }, xUValues.getShape());

        /*
         * as discussed, dataset uses the horizontal grid to fetch data not the
         * curvilinear grid so the data it can fetch is limited. Use readFeature
         * method to extract data instead of extractMapFeature method at moment.
         */

        /*
         * Variable xU =cdf.findVariable("allx_u"); ArrayFloat uValues
         * =(ArrayFloat) xU.read(); for (int m = 0; m < xiSize; m++) { for (int
         * n = 0; n < etaSize; n++) { Number number = xUValues.get(m, n);
         * assertEquals(uValues.getFloat(n+m*etaSize), number.floatValue(),
         * delta); } }
         */
    }
}
