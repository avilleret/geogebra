package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;

/**
 *LimitBelow
 */
class CmdLimitBelow extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimitBelow(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction()) && (arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.LimitBelow(c.getLabel(),
						(GeoFunction) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
