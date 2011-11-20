package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.main.MyError;

/**
 * TurningPoint[ <GeoFunction> ]
 */
class CmdTurningPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTurningPoint(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.TurningPoint(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "TurningPoint", arg[0]);

		default:
			throw argNumErr(app, "TurningPoint", n);
		}
	}
}