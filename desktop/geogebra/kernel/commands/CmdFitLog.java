package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/** 
 * FitLog[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */

class CmdFitLog extends CmdOneListFunction{

    public CmdFitLog(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLog(a, b);
	}

}// class CmdFitLog
