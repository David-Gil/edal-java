/*******************************************************************************
 * Copyright (c) 2013 The University of Reading
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

package uk.ac.rdg.resc.edal.metadata;

import uk.ac.rdg.resc.edal.domain.DiscreteHorizontalDomain;
import uk.ac.rdg.resc.edal.domain.HorizontalDomain;
import uk.ac.rdg.resc.edal.domain.TemporalDomain;
import uk.ac.rdg.resc.edal.domain.VerticalDomain;
import uk.ac.rdg.resc.edal.grid.HorizontalCell;
import uk.ac.rdg.resc.edal.grid.TimeAxis;
import uk.ac.rdg.resc.edal.grid.VerticalAxis;

import java.io.Serializable;

/**
 * {@link VariableMetadata} whose {@link VerticalDomain} and
 * {@link TemporalDomain} are discrete axes, and whose {@link HorizontalDomain}
 * is a {@link DiscreteHorizontalDomain} (but not necessarily a grid)
 * 
 * @author Guy Griffiths
 */
public class DiscreteLayeredVariableMetadata extends VariableMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    public DiscreteLayeredVariableMetadata(Parameter parameter,
            DiscreteHorizontalDomain<? extends HorizontalCell> hDomain, VerticalAxis zDomain,
            TimeAxis tDomain, boolean scalar) {
        super(parameter, hDomain, zDomain, tDomain, scalar);
        if (hDomain == null) {
            throw new IllegalArgumentException(
                    "GridVariableMetadata must contain a horizontal domain");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DiscreteHorizontalDomain<? extends HorizontalCell> getHorizontalDomain() {
        return (DiscreteHorizontalDomain<HorizontalCell>) super.getHorizontalDomain();
    }

    /**
     * Returns the {@link VerticalAxis} of the variable
     */
    @Override
    public VerticalAxis getVerticalDomain() {
        return (VerticalAxis) super.getVerticalDomain();
    }

    /**
     * Returns the {@link TimeAxis} of the variable
     */
    @Override
    public TimeAxis getTemporalDomain() {
        return (TimeAxis) super.getTemporalDomain();
    }
}
