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
import geogebra.kernel.geos.GeoFunction;


/**
 * upper sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumLower extends AlgoFunctionAreaSums {
		
	/**
	 * Creates lower sum
	 * @param cons construction
	 * @param label label
	 * @param f function
	 * @param a lower bound
	 * @param b upper bound
	 * @param n number of columns
	 */
	public AlgoSumLower(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, AlgoFunctionAreaSums.TYPE_LOWERSUM);		
		cons.registerEuclidianViewCE(this);
	}
	
	private AlgoSumLower( 
			   NumberValue a, NumberValue b, NumberValue n,double[]vals,double[]borders) {
		super(a, b, n, AlgoFunctionAreaSums.TYPE_LOWERSUM,vals,borders);		
		((Construction) cons).registerEuclidianViewCE(this);
	}
	
	public AlgoSumLower copy() {
		return new AlgoSumLower((NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),(NumberValue)this.getN().copy().evaluate(),
				getValues().clone(),getLeftBorder().clone());
	}
	@Override
	public String getClassName() {
		return "AlgoSumLower";
	}
	
}
