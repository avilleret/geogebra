package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;

/*
 * Name[ <GeoElement> ]
 */
public class CmdName extends CommandProcessor {

	public CmdName(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Name[ <GeoElement> ]
			arg = resArgs(c);			
			GeoElement[] ret = { kernel.Name(c.getLabel(),
								arg[0]) };
			return ret;


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
