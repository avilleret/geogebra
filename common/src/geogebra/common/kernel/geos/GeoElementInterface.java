package geogebra.common.kernel.geos;

import geogebra.common.kernel.AbstractConstruction;

import java.util.List;

public interface GeoElementInterface {
	public boolean needsReplacingInExpressionNode();
	public String getLabel();
	public String getRealLabel();	
	public boolean isLabelSet();
	public boolean isIndependent();
	public boolean isLocalVariable();
	public boolean isGeoVector();
	public boolean isGeoLine();
	public boolean isNumberValue();
	public String toValueString();
	public String getAlgebraDescriptionTextOrHTML();
	public String getNameDescriptionTextOrHTML();
	public String getCaptionDescriptionHTML(boolean b);
	public int getConstructionIndex();
	public String getDefinitionDescription();
	public String getCommandDescription();
	public String getAlgebraDescription();
	public String getNameDescription();
	public String getAlgebraDescriptionHTML(boolean b);
	public String getCommandDescriptionHTML(boolean b);
	public String getDefinitionDescriptionHTML(boolean b);
	public boolean isConsProtocolBreakpoint();
	public void setConsProtocolBreakpoint(boolean newVal);
	public int getRelatedModeID();
	public void update();
	public boolean setCaption(String string);
	public String getNameDescriptionHTML(boolean b, boolean c);
	public boolean isChildOf(GeoElementInterface parent);
	public int getMinConstructionIndex();
	public boolean isGeoNumeric();
	public List<Integer> getViewSet();
	public void setRandomGeo(boolean b);
	public boolean isAnimating();
	public boolean isGeoList();
	public boolean isDefined();
	public void setLabel(String label);
	public GeoElementInterface copyInternal(AbstractConstruction cons);	
	
}