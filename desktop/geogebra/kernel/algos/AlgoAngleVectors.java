/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoAngle;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoVector;

public class AlgoAngleVectors extends AlgoElement {

	private GeoVector v, w; // input
    private GeoAngle angle; // output           

    public AlgoAngleVectors(
        Construction cons,
        String label,
        GeoVector v,
        GeoVector w) {
        super(cons);
        this.v = v;
        this.w = w;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
        angle.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoAngleVectors";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;
        input[1] = w;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public GeoAngle getAngle() {
        return angle;
    }
    public GeoVector getv() {
        return v;
    }
    public GeoVector getw() {
        return w;
    }

    // calc angle between vectors v and w
    // angle in range [0, 2pi) 
    // use normalvector to 
    @Override
	public final void compute() {    	    	    	
    	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = v.x * w.y - v.y * w.x;
    	double prod = v.x * w.x + v.y * w.y;    	    
    	double value = Math.atan2(det, prod);                  	    	
        angle.setValue(value);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("AngleBetweenAB",v.getLabel(),w.getLabel());

    }
}
