package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.main.MyError;

/**
 * TaylorSeries[ <GeoFunction>, <Number>, <Number> ]
 */
class CmdTaylorSeries extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTaylorSeries(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.TaylorSeries(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else
				throw argErr(app, "TaylorSeries", null);

		default:
			throw argNumErr(app, "TaylorSeries", n);
		}
	}
}