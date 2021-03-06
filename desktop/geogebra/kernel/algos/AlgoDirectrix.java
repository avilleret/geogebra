/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDirectrix.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVec2D;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDirectrix extends AlgoElement {

    private GeoConic c; // input
    private GeoLine directrix; // output          

    private GeoVec2D[] eigenvec;
    private GeoVec2D b;
    private GeoPoint P;

    public AlgoDirectrix(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;

        eigenvec = c.eigenvec;
        b = c.b;

        directrix = new GeoLine(cons);
        P = new GeoPoint(cons);
        directrix.setStartPoint(P);

        setInputOutput(); // for AlgoElement                
        compute();
        directrix.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDirectrix";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        super.setOutputLength(1);
        super.setOutput(0, directrix);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getDirectrix() {
        return directrix;
    }
    GeoConic getConic() {
        return c;
    }

    // calc axes
    @Override
	public final void compute() {
        // only parabola has directrix
        if (c.type == GeoConic.CONIC_PARABOLA) {
            // directrix has direction of second eigenvector
            // through point (b - p/2* eigenvec1)        
            directrix.x = -eigenvec[1].y;
            directrix.y = eigenvec[1].x;
            double px = b.x - c.p / 2.0 * eigenvec[0].x;
            double py = b.y - c.p / 2.0 * eigenvec[0].y;
            directrix.z = - (directrix.x * px + directrix.y * py);

            P.setCoords(px, py, 1.0);
        } else
            directrix.setUndefined();
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DirectrixOfA",c.getLabel());
    }
}
