/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;

/**
 *
 * @author  Michael
 * @version 
 */
public class AlgoDynamicCoordinates extends AlgoElement {

    private NumberValue x,y; // input
	private GeoPoint P; // input
    private GeoPoint M; // output        

	
    public AlgoDynamicCoordinates(Construction cons, String label, GeoPoint P, NumberValue x, NumberValue y) {
        super(cons);
        this.P = P;
        this.x = x;
        this.y = y;
        // create new Point
        M = new GeoPoint(cons);
        setInputOutput();

        compute();        
        M.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDynamicCoordinates";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = P;
        input[1] = (GeoElement)x.toGeoElement();
        input[2] = (GeoElement)y.toGeoElement();

        super.setOutputLength(1);
        super.setOutput(0, M);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint getPoint() {
        return M;
    }

    public GeoPoint getParentPoint() {
        return P;
    }

    // calc midpoint
    @Override
	public final void compute() {
    	
    	double xCoord = x.getDouble();
    	double yCoord = y.getDouble();
    	
    	if (Double.isNaN(xCoord) || Double.isInfinite(xCoord) ||
    			Double.isNaN(yCoord) || Double.isInfinite(yCoord)) {
    		P.setUndefined();
    		return;
    	}
    	
    	M.setCoords(xCoord, yCoord, 1.0);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("DynamicCoordinatesOfA",P.getLabel());
    }
}
