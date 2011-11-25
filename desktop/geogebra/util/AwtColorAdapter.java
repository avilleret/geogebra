package geogebra.util;

import geogebra.common.util.ColorAdapter;

import java.awt.Color;
public class AwtColorAdapter extends java.awt.Color implements ColorAdapter{
	
	public AwtColorAdapter(Color color){
		super(color.getRed(), color.getGreen(), color.getBlue());
		
	}	
}
