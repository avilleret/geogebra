/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.kernel.kernelND.GeoSegmentND;


/**
 * Creates a Polygon from a given list of points or point array.
 * 
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoPolygon extends AlgoElement {

	protected GeoPointND [] points;  // input
	protected GeoList geoList;  // alternative input
    protected GeoPolygon poly;     // output
    
    /** /2D coord sys used for 3D */
    protected CoordSys cs2D; 
    
    /** polyhedron (when segment is part of), used for 3D */
    protected GeoElement polyhedron; 
    
    /** normal direction, used for 3D */
    protected GeoDirectionND direction;
    
    
    public AlgoPolygon(Construction cons, String [] labels, GeoList geoList) {
    	this(cons, labels, null, geoList);
    }
    
    public AlgoPolygon(Construction cons, String [] labels, GeoPointND [] points) {
    	this(cons, labels, points, null);
    }
 
    protected AlgoPolygon(Construction cons, String [] labels, GeoPointND [] points, GeoList geoList) {
    	this(cons,labels,points,geoList,null,true,null,null);
    }
    
    /**
     * @param cons the construction
     * @param labels names of the polygon and the segments
     * @param points vertices of the polygon
     * @param geoList list of vertices of the polygon (alternative to points)
     * @param cs2D for 3D stuff : GeoCoordSys2D
     * @param createSegments  says if the polygon has to creates its edges (3D only) 
     * @param polyhedron polyhedron (when segment is part of), used for 3D
     * @param direction normal direction, used for 3D
     */
    protected AlgoPolygon(Construction cons, String [] labels, 
    		GeoPointND [] points, GeoList geoList, CoordSys cs2D, 
    		boolean createSegments, GeoElement polyhedron, GeoDirectionND direction) {
        super(cons);
        this.points = points;           
        this.geoList = geoList;
        this.cs2D = cs2D;
        this.polyhedron = polyhedron;
        this.direction = direction;
          
        //poly = new GeoPolygon(cons, points);
        createPolygon(createSegments);  
        
        // compute polygon points
        compute();  
        
        setInputOutput(); // for AlgoElement
        
        //G.Sturr 2010-3-14: Do not label segments or points for polygons 
        // formed by a geolist. 
        //(current code cannot handle sequences of variable length)
        
        //poly.initLabels(labels);
        if(geoList == null){
        	poly.initLabels(labels);
        }else{
        	if(labels!=null)
        		poly.setLabel(labels[0]);
        }
        
        //END G.Sturr        
    }   
      
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    protected void createPolygon(boolean createSegments){
    	poly = new GeoPolygon(this.cons, this.points);
    }
        
    @Override
	public String getClassName() {
        return "AlgoPolygon";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLYGON;
    }
    
    /**
     * Update point array of polygon using the given array list
     * @param pointList
     */
    private void updatePointArray(GeoList pointList) {
    	// check if we have a point list
    	if (pointList.getElementType() != GeoClass.POINT) {
    		poly.setUndefined();
    		return;
    	}
    	
    	// remember old number of points
    	int oldPointsLength = points == null ? 0 : points.length;
    	
    	// create new points array
    	int size = pointList.size();
    	points = new GeoPoint[size];
    	for (int i=0; i < size; i++) {    		
    		points[i] = (GeoPoint) pointList.get(i);
    	}
    	poly.setPoints(points);
    	
    	if (oldPointsLength != points.length)
    		setOutput();    	
    }

    protected GeoElement [] createEfficientInput(){

    	GeoElement [] efficientInput;

    	if (geoList != null) {
    		// list as input
    		efficientInput = new GeoElement[1];
    		efficientInput[0] = geoList;
    	} else {    	
    		// points as input
    		efficientInput = new GeoElement[points.length];
    		for(int i = 0; i < points.length; i++)
    			efficientInput[i]=(GeoElement) points[i];
    	}    

    	return efficientInput;
    }    
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	//efficient inputs are points or list
    	GeoElement [] efficientInput = createEfficientInput();  	
    	
    	//add polyhedron to inputs
    	if (polyhedron==null){
    		input=efficientInput;  		
    	}else{
    		input = new GeoElement[efficientInput.length+1];
    		for (int i=0; i<efficientInput.length; i++)
    			input[i]=efficientInput[i];
    		input[efficientInput.length]=polyhedron;
    	}
    	    	
    	setEfficientDependencies(input, efficientInput);
        
    	//set output after, to avoid segments to have this to parent algo
    	setOutput(); 	
    	
    	// parent of output
        poly.setParentAlgorithm(this);       
        ((Construction) cons).addToAlgorithmList(this); 
    }    
        
    private void setOutput() {
    	GeoSegmentND [] segments = poly.getSegments();
    	int size = 1;
    	if (segments!=null) {
    		size+=segments.length;
    	}
    	
        super.setOutputLength(size);
        super.setOutput(0, poly);
        
        for (int i=0; i < size-1; i++) {
            super.setOutput(i+1, (GeoElement) segments[i]);
        }
        
        /*
        String s="output = ";
        for (int i=0; i < size-1; i++) {
            s+=output[i].getLabel()+", ";
        } 
        Application.debug(s);
        */       
    }
    
    @Override
	public void update() {
        // compute output from input
        compute();
        super.getOutput(0).update();
    }
      
    public GeoPolygon getPoly() { 
    	return poly; 
    }  
    
    public GeoPoint [] getPoints() {
    	return (GeoPoint[]) points;
    }
    
    public GeoElement getPolyhedron() { return polyhedron; }    
    
    @Override
	public void remove() {
        super.remove();
        //if polygon is part of a polyhedron, remove it
        if (polyhedron != null)
            polyhedron.remove();
    }  
       
    @Override
	public void compute() { 
    	if (geoList != null) {
    		updatePointArray(geoList);
    	}
    	
        // compute area
        poly.calcArea(); 
        
        // update region coord sys
        poly.updateRegionCS();
    }      
    
    protected StringBuilder sb;   

    protected void createStringBuilder(){
    	
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
  
        sb.append(app.getPlain("Polygon"));
        sb.append(' ');
        
        //G.Sturr: get label from geoList  (2010-3-15)
		if (geoList != null) {
			sb.append(geoList.getLabel());
			
		} else {
		// use point labels
			 
			int last = points.length - 1;
			for (int i = 0; i < last; i++) {
				sb.append(points[i].getLabel());
				sb.append(", ");
			}
			sb.append(points[last].getLabel());
		}        

        if (polyhedron!=null){
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        	sb.append(polyhedron.getLabel());
        }       
    }

    @Override
	final public String toString() {
    	createStringBuilder();
    	return  sb.toString();
    }
        	
    
}
