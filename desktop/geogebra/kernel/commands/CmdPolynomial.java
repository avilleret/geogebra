package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoList;

/**
 * Polynomial[ <GeoFunction> ]
 */
class CmdPolynomial extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolynomial(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (ok[0] = (arg[0].isGeoFunctionable())) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction()) };
				return ret;
			}
			// Michael Borcherds 2008-01-22 BEGIN
			// PolynomialFromCoordinates
			else if (ok[0] = (arg[0].isGeoList())) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						((GeoList) arg[0])) };
				return ret;
			}
			// Michael Borcherds 2008-01-22 END
			else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			// Markus Hohenwarter 2008-01-26 BEGIN
			// try to create list of points
			GeoList list = wrapInList(kernel, arg, arg.length,
					GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						list) };
				return ret;
			}
			// Markus Hohenwarter 2008-01-26 END
			else
				throw argNumErr(app, c.getName(), n);
		}
	}
}
