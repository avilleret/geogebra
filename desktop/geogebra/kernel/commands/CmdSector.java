package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;

/**
 * Sector[ <GeoConic>, <Number>, <Number> ] Sector[ <GeoConic>, <GeoPoint>,
 * <GeoPoint> ]
 */
class CmdSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.ConicSector(c.getLabel(),
						(GeoConic) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.ConicSector(c.getLabel(), (GeoConic) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Sector", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Sector", arg[1]);
				else
					throw argErr(app, "Sector", arg[2]);
			}

		default:
			throw argNumErr(app, "Sector", n);
		}
	}
}
