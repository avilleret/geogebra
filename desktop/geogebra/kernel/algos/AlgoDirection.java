/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDirection.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDirection extends AlgoElement {

    private GeoLine g; // input
    private GeoVector v; // output  

    /** Creates new AlgoDirection */    
    public AlgoDirection(Construction cons, GeoLine g) {
    	this(cons, null, g);
    }
    
    public AlgoDirection(Construction cons, String label, GeoLine g) {
        super(cons);
        this.g = g;
        v = new GeoVector(cons);

        GeoPoint possStartPoint = g.getStartPoint();                  
        if (possStartPoint!= null && possStartPoint.isLabelSet()) {
	        try{
	            v.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }

        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        v.z = 0.0d;
        compute();        
        v.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDirection";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = g;

        super.setOutputLength(1);
        super.setOutput(0, v);
        setDependencies(); // done by AlgoElement
    }

    public GeoVector getVector() {
        return v;
    }
    GeoLine getg() {
        return g;
    }

    // direction vector of g
    @Override
	public final void compute() {
        v.x = g.y;
        v.y = -g.x;
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DirectionOfA",g.getLabel());
    }
}
