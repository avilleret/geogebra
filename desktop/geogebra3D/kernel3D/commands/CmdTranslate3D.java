package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdAngle;
import geogebra.kernel.commands.CmdTranslate;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.kernel.geos.GeoVec3D;
import geogebra.kernel.geos.GeoVector;
import geogebra.kernel.geos.Translateable;
import geogebra.kernel.kernelND.GeoVectorND;

public class CmdTranslate3D extends CmdTranslate {
	
	
	
	
	public CmdTranslate3D(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError,
	CircularDefinitionException {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			//check if there is a 3D geo
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
				
				// translate object

				/*
				if ((ok[0] = (arg[0].isGeoVector()))
						&& (ok[1] = (arg[1].isGeoPoint()))) {
					GeoVector v = (GeoVector) arg[0];
					GeoPoint P = (GeoPoint) arg[1];

					ret[0] = kernel.Translate(label, v, P);

					return ret;
				} else */
				if ((ok[0] = (arg[0] instanceof Translateable
						|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
						&& (ok[1] = (arg[1].isGeoVector()))) {				
					ret = kernel.getManager3D().Translate3D(label, arg[0], (GeoVectorND) arg[1]);
					return ret;
				}
			}
			break;
		}
		
	    return super.process(c);
	}
	
}
