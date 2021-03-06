package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * HarmonicMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdRootMeanSquare extends CmdOneListFunction {

	public CmdRootMeanSquare(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.RootMeanSquare(a, b);
	}


}
