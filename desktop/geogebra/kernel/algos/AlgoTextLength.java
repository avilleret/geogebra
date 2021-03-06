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
import geogebra.kernel.geos.GeoText;


/**
 * Length of a GeoText object.
 * @author Michael
 */

public class AlgoTextLength extends AlgoElement {

	private GeoText text; //input
    private GeoNumeric length; //output	

    public AlgoTextLength(Construction cons, String label, GeoText text) {
        super(cons);
        this.text = text;
               
        length = new GeoNumeric(cons);

        setInputOutput();
        compute();
        length.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoTextLength";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = text;

        super.setOutputLength(1);
        super.setOutput(0, length);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getLength() {
        return length;
    }

    @Override
	public final void compute() {
    	if (text.isDefined())
    		length.setValue(text.getTextString().length());
    	else 
    		length.setUndefined();
    }
    
}
