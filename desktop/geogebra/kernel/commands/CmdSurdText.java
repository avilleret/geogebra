package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;

/**
 *SurdText
 */
class CmdSurdText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSurdText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.SurdText(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernel.SurdText(c.getLabel(),
						(GeoPoint) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
