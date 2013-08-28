package uk.ac.rdg.resc.edal.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;

import uk.ac.rdg.resc.edal.domain.MapDomain;
import uk.ac.rdg.resc.edal.domain.MapDomainImpl;
import uk.ac.rdg.resc.edal.feature.MapFeature;
import uk.ac.rdg.resc.edal.geometry.BoundingBox;
import uk.ac.rdg.resc.edal.geometry.BoundingBoxImpl;
import uk.ac.rdg.resc.edal.graphics.style.ArrowLayer;
import uk.ac.rdg.resc.edal.graphics.style.ColourMap;
import uk.ac.rdg.resc.edal.graphics.style.ColourScale;
import uk.ac.rdg.resc.edal.graphics.style.ColourScheme;
import uk.ac.rdg.resc.edal.graphics.style.ColourScheme2D;
import uk.ac.rdg.resc.edal.graphics.style.ContourLayer;
import uk.ac.rdg.resc.edal.graphics.style.ContourLayer.ContourLineStyle;
import uk.ac.rdg.resc.edal.graphics.style.MapImage;
import uk.ac.rdg.resc.edal.graphics.style.PaletteColourScheme;
import uk.ac.rdg.resc.edal.graphics.style.PatternScale;
import uk.ac.rdg.resc.edal.graphics.style.Raster2DLayer;
import uk.ac.rdg.resc.edal.graphics.style.RasterLayer;
import uk.ac.rdg.resc.edal.graphics.style.SmoothedContourLayer;
import uk.ac.rdg.resc.edal.graphics.style.StippleLayer;
import uk.ac.rdg.resc.edal.graphics.style.ThresholdColourScheme2D;
import uk.ac.rdg.resc.edal.graphics.style.util.FeatureCatalogue;
import uk.ac.rdg.resc.edal.graphics.style.util.GlobalPlottingParams;
import uk.ac.rdg.resc.edal.grid.HorizontalGrid;
import uk.ac.rdg.resc.edal.grid.RegularGridImpl;
import uk.ac.rdg.resc.edal.util.Array2D;

/**
 * These tests plot a predictable (in-memory) dataset with missing values, and
 * out-of-range data. The expected outputs (found in src/test/resources) have
 * been examined and are correct for the dataset.
 * 
 * The tests here generate the images and do a pixel-by-pixel comparison with
 * the expected images. If any of these tests fail, it means that the plotting
 * output has changed since a careful examination showed the test images to be
 * correct.
 * 
 * This would be expected if a plot style has changed at all, in which case the
 * new image can be manually examined substituted for the test example if
 * correct.
 * 
 * @author Guy
 */
public class PlotTests {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 250;
    /* The feature to test against */
    private MapFeature testFeature;
    /* Dummy variables needed for plotting */
    private FeatureCatalogue catalogue;
    private GlobalPlottingParams params;
    /* ColourScale which can be used by any plotting layer which needs it */
    private ColourScale scale = new ColourScale(0f, 1f, false);

    @Before
    public void setup() {
        BoundingBox bbox = new BoundingBoxImpl(-180, -90, 180, 90, DefaultGeographicCRS.WGS84);

        HorizontalGrid hGrid = new RegularGridImpl(bbox, WIDTH, HEIGHT);
        MapDomain domain = new MapDomainImpl(hGrid, null, null, null);
        Map<String, Array2D> valuesMap = new HashMap<String, Array2D>();

        /*
         * These reference static vars defined at the end of the file (for
         * readability)
         */
        valuesMap.put("testvar", arr);
        valuesMap.put("testvarx", xarr);
        valuesMap.put("testvary", yarr);
        valuesMap.put("testvarth", thetaarr);
        /*
         * This test feature now has one variable named "testvar" which varies
         * between 0 and 1
         */
        testFeature = new MapFeature("testfeature", "Test Feature",
                "This is a feature used for testing", domain, null, valuesMap);

        catalogue = new FeatureCatalogue() {
            @Override
            public MapFeatureAndMember getFeatureAndMemberName(String id,
                    GlobalPlottingParams params) {
                if (id.equals("test")) {
                    return new MapFeatureAndMember(testFeature, "testvar");
                } else if (id.equals("xtest")) {
                    return new MapFeatureAndMember(testFeature, "testvarx");
                } else if (id.equals("ytest")) {
                    return new MapFeatureAndMember(testFeature, "testvary");
                } else if (id.equals("thetatest")) {
                    return new MapFeatureAndMember(testFeature, "testvarth");
                } else {
                    return null;
                }
            }
        };
        params = new GlobalPlottingParams(WIDTH, HEIGHT, bbox, null, null, null, null);

    }

    /*
     * Convenience method for reading a test image for comparison
     */
    private BufferedImage getComparisonImage(String name) {
        URL url = this.getClass().getResource("/" + name + ".png");
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            fail("Comparison image (" + name + ") not available.");
            return null;
        }
    }

    /*
     * Compares two images on a pixel-by-pixel basis
     */
    private void compareImages(BufferedImage expected, BufferedImage actual) {
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getHeight(), actual.getHeight());
        for (int i = 0; i < actual.getWidth(); i++) {
            for (int j = 0; j < actual.getHeight(); j++) {
                assertEquals(expected.getRGB(i, j), actual.getRGB(i, j));
            }
        }
    }

    @Test
    public void testRaster() {
        ColourScheme colourScheme = new PaletteColourScheme(scale, new ColourMap(Color.blue,
                Color.red, new Color(0, true), "#000000,#00ff00", 10));
        RasterLayer rasterLayer = new RasterLayer("test", colourScheme);
        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(rasterLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("raster");
        compareImages(comparisonImage, image);
    }

    @Test
    public void testContour() {
        ContourLayer contourLayer = new ContourLayer("test", scale, false, 5, Color.cyan, 1,
                ContourLineStyle.SOLID, true);
        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(contourLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("contour");
        compareImages(comparisonImage, image);
    }

    @Test
    public void testSmoothedContour() {
        SmoothedContourLayer smoothedContourLayer = new SmoothedContourLayer("test", scale, false,
                5, Color.red, 1, ContourLineStyle.SOLID, true);
        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(smoothedContourLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("smoothed_contour");
        compareImages(comparisonImage, image);
    }

    @Test
    public void testStipple() {
        PatternScale pattern = new PatternScale(10, 0f, 1f, false);
        StippleLayer stippleLayer = new StippleLayer("test", pattern);
        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(stippleLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("stipple");
        compareImages(comparisonImage, image);
    }

    @Test
    public void testRaster2D() {
        ColourScheme2D colourScheme = new ThresholdColourScheme2D(Arrays.asList(0.1f, 0.5f, 0.9f),
                Arrays.asList(0.1f, 0.5f, 0.9f), Arrays.asList(new Color(0, 0, 0), new Color(0,
                        100, 0), new Color(0, 200, 0), new Color(0, 255, 0), new Color(100, 0, 0),
                        new Color(100, 100, 0), new Color(100, 200, 0), new Color(100, 255, 0),
                        new Color(200, 0, 0), new Color(200, 100, 0), new Color(200, 200, 0),
                        new Color(200, 255, 0), new Color(255, 0, 0), new Color(255, 100, 0),
                        new Color(255, 200, 0), new Color(255, 255, 0)), new Color(0, true));
        Raster2DLayer raster2dLayer = new Raster2DLayer("xtest", "ytest", colourScheme);

        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(raster2dLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("raster2d");
        compareImages(comparisonImage, image);
    }

    @Test
    public void testArrow() {
        ArrowLayer arrowLayer = new ArrowLayer("thetatest", 8, Color.black);
        MapImage mapImage = new MapImage();
        mapImage.getLayers().add(arrowLayer);
        BufferedImage image = mapImage.drawImage(params, catalogue);
        BufferedImage comparisonImage = getComparisonImage("arrow");
        compareImages(comparisonImage, image);
    }

    /*
     * Static arrays defined at the bottom to stay out of the way
     */

    private final static Array2D arr = new Array2D(HEIGHT, WIDTH) {
        @Override
        public void set(Number value, int... coords) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Number get(int... coords) {
            int x = coords[1];
            int y = coords[0];
            if (x == WIDTH / 2 || y == HEIGHT / 2) {
                return null;
            }
            if (x == WIDTH / 4 || y == HEIGHT / 4) {
                return -0.5;
            }
            if (x == 3 * WIDTH / 4 || y == 3 * HEIGHT / 4) {
                return 1.5;
            }
            double xComp = ((double) x) / WIDTH;
            double yComp = ((double) y) / HEIGHT;
            return xComp * yComp;
        }
    };

    private final static Array2D xarr = new Array2D(HEIGHT, WIDTH) {
        @Override
        public void set(Number value, int... coords) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Number get(int... coords) {
            return ((double) coords[1]) / WIDTH;
        }
    };

    private final static Array2D yarr = new Array2D(HEIGHT, WIDTH) {
        @Override
        public void set(Number value, int... coords) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Number get(int... coords) {
            return ((double) coords[0]) / HEIGHT;
        }
    };

    private final static Array2D thetaarr = new Array2D(HEIGHT, WIDTH) {
        @Override
        public void set(Number value, int... coords) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Number get(int... coords) {
            return 2*Math.PI*((double) coords[1]) / WIDTH;
        }
    };
}