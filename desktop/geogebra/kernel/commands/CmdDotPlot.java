package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/**
 * DotPlot[ <List of Numeric> ] G.Sturr 2010-8-10
 */
class CmdDotPlot extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDotPlot(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b) {
		return kernel.DotPlot(a, b);
	}

}
