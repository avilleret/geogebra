package geogebra.kernel.commands;

import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;

/**
 * Distance[ <GeoPoint>, <GeoPoint> ] Distance[ <GeoPoint>, <GeoLine> ]
 * Distance[ <GeoLine>, <GeoPoint> ] Distance[ <GeoLine>, <GeoLine> ]
 */
class CmdDistance extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDistance(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPoint) arg[0], arg[1]) };
				return ret;
			}

			// distance between line and point
			else if (arg[1].isGeoPoint()) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPoint) arg[1], arg[0]) };
				return ret;
			}

			// distance between line and line
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoLine) arg[0], (GeoLine) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, "Distance", arg[1]);
				else
					throw argErr(app, "Distance", arg[0]);
			}

		default:
			throw argNumErr(app, "Distance", n);
		}
	}
}
