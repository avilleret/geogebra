package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;

/*
 * LogNormal distribution
 */
class CmdLogNormal extends CommandProcessor {

	public CmdLogNormal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<0,0,1/2 erf((ln(x)-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2]", true );
						
						return ret;
						
					} else {
						GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<0,0,1/(x sqrt(2 * pi) * abs("+sd+"))*exp(-((ln(x)-("+mean+"))^2/(2*("+sd+")^2)))]", true );
						
						return ret;
					}
					
				} else if (arg[2].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					String x = arg[2].getLabel();
					kernel.restorePrintAccuracy();
					GeoElement[] ret = (GeoElement[])kernel.getAlgebraProcessor().processAlgebraCommand( "1/2 erf((ln(If["+x+"<0,0,"+x+"])-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2", true );
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
