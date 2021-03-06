/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoAngle;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleConic extends AlgoElement {

    private GeoConic c; // input
    private GeoAngle angle; // output                  

    public AlgoAngleConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        angle.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoAngleConic";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public GeoAngle getAngle() {
        return angle;
    }
    
    GeoConic getConic() {
        return c;
    }

    // compute conic's angle
    @Override
	public final void compute() {
        // take a look at first eigenvector
        angle.setValue(Math.atan2(c.eigenvec[0].y, c.eigenvec[0].x));
    }

    @Override
	public final String toString() {
    	return app.getPlain("AngleOfA",c.getLabel());
    }
}
