/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoPoint;


/**
 * Finds all local extrema of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExtremumPolynomial extends AlgoRootsPolynomial {

    public AlgoExtremumPolynomial(
        Construction cons,
        String[] labels,
        GeoFunction f) {
        super(cons, labels, f);
        
        
       // Application.debug("AlgoExtremumPolynomial: " + f + ", " + f.cons);
       // Iterator it = f.getVariables().iterator();
       // while (it.hasNext()) {
       // 	GeoElement var = (GeoElement) it.next();
       // 	Application.debug("  " + var + ", " + var.cons );
       // }
    }

    @Override
	public String getClassName() {
        return "AlgoExtremumPolynomial";
    }

    public GeoPoint[] getExtremumPoints() {
        return super.getRootPoints();
    }

    @Override
	public final void compute() {
        if (f.isDefined()) {
            // TODO: remove
            //Application.debug("*** extremum of " + f);
                    	
            yValFunction = f.getFunction();       
            
            // roots of first derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 1);             
        } else {
            curRealRoots = 0;
        }

        setRootPoints(curRoots, curRealRoots);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ExtremumOfA",f.getLabel());

    }

}
