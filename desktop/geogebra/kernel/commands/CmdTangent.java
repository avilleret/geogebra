package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoCurveCartesian;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.implicit.GeoImplicitPoly;

/**
 * Tangent[ <GeoPoint>, <GeoConic> ] Tangent[ <GeoLine>, <GeoConic> ] Tangent[
 * <NumberValue>, <GeoFunction> ] Tangent[ <GeoPoint>, <GeoFunction> ] Tangent[
 * <GeoPoint>, <GeoCurveCartesian> ] Tangent[<GeoPoint>,<GeoImplicitPoly>]
 * Tangent[ <GeoLine>, <GeoImplicitPoly>]
 */
class CmdTangent extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTangent(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// tangents through point
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return kernel.Tangent(c.getLabels(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint())))
				return kernel.Tangent(c.getLabels(), (GeoPoint) arg[1],
						(GeoConic) arg[0]);
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return kernel.Tangent(c.getLabels(), (GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(NumberValue) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction()) };
				return ret;
			}

			// tangents of function at x = x(Point P)
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction()) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[1], ((GeoFunctionable) arg[0])
								.getGeoFunction()) };
				return ret;
			}
			// Victor Franco 11-02-2007: for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			}
			// Victor Franco 11-02-2007: end for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoPoint) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[1] = (arg[1].isGeoPoint()))
					&& (ok[0] = (arg[0].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoPoint) arg[1], (GeoImplicitPoly) arg[0]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoLine) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return kernel.CommonTangents(c.getLabels(), (GeoConic) arg[0], (GeoConic) arg[1]);
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Tangent", arg[0]);
				else
					throw argErr(app, "Tangent", arg[1]);
			}

		default:
			throw argNumErr(app, "Tangent", n);
		}
	}
}
