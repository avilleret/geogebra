/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVector;


public class AlgoPointVector extends AlgoElement {

    private GeoPoint P;  // input
    private GeoVector v; // input
    private GeoPoint Q;     // output       
        
    public AlgoPointVector(Construction cons, String label, GeoPoint P, GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;         
        Q = new GeoPoint(cons); 
        
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        Q.setLabel(label);
    }   
    
    @Override
	public String getClassName() {
        return "AlgoPointVector";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_VECTOR_FROM_POINT;
    }
      
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;
        
        super.setOutputLength(1);
        super.setOutput(0, Q);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint getQ() { return Q; }
    
    @Override
	public final void compute() {
        Q.setCoords(P.inhomX + v.x, P.inhomY + v.y, 1.0);
    }   
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("PointAplusB",input[0].getLabel(),input[1].getLabel());

    }
}
