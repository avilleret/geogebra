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
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoNumeric;


/**
 * AlgoMaximize
 * Command Minimize[ <dependent variable>, <independent variable> ] (and Minimize[] )
 * which searches for the independent variable which gives the 
 * largest result for the dependent variable.
 * 
 *  Extends abstract class AlgoOptimize
 *  
 * @author 	Hans-Petter Ulven
 * @version 20.02.2011
 * 
 */

public class AlgoMaximize extends AlgoOptimize{

	/** Constructor for Maximize*/
	public AlgoMaximize(Construction cons,String label,GeoElement dep,GeoNumeric indep){
		super(cons,label,dep,indep,AlgoOptimize.MAXIMIZE);
		//cons.registerEuclididanViewAlgo(this);
	}//Constructor for Maximize
	

    @Override
	public String getClassName() {
    	return "AlgoMaximize";
    }//getClassName()    

	

}//class AlgoMaximize