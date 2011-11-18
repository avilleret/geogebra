/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.kernel.kernelND.GeoSegmentND;

/**
 * Algorithm to compute the circumference of a
 * {@link geogebra.kernel.geos.GeoPolygon GeoPolygon}.
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class AlgoPerimeterPoly extends AlgoElement {

	// Take a polygon as input
	private GeoPolygon polygon;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric circum;

	public AlgoPerimeterPoly(Construction cons, String label, GeoPolygon polygon) {
		this(cons, polygon);
		circum.setLabel(label);
	}

	AlgoPerimeterPoly(Construction cons, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;

		circum = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public String getClassName() {
		return "AlgoPerimeterPoly";
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = polygon;

		super.setOutputLength(1);
        super.setOutput(0, circum);
		setDependencies();
	}

	/**
	 * Compute circumference by adding up the length of it's segemnts.
	 */
	@Override
	public final void compute() {
		if (!polygon.isDefined()) {
			circum.setUndefined();
			return;
		}
				
		GeoSegmentND[] segment = polygon.getSegments();
		double length = 0;
		for (int i = 0; i < segment.length; i++) {
			length = length + (segment[i].getLength());		
		}
		circum.setValue(length);
	}

	/**
	 * Get the GeoPolygon's circumference.
	 * 
	 * @return circumference
	 */
	public GeoNumeric getCircumference() {
		return circum;
	}	
}