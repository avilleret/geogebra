package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoText;

/*
 * Last[ <List>,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdLast extends CommandProcessor {

	public CmdLast(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.Last(c.getLabel(),
						(GeoList) arg[0], null ) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = { 
						kernel.Last(c.getLabel(),
						(GeoText) arg[0], null ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		case 2:
			boolean list = arg[0].isGeoList();
			boolean text = arg[0].isGeoText();
			if ( list && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.Last(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else if ( text && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.Last(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), (list && text) ? arg[1] : arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
