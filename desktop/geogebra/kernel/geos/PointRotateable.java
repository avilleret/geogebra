package geogebra.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPoint S);
}
