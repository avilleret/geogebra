package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.Operation;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.algos.AlgoBoxPlot;
import geogebra.kernel.algos.AlgoClasses;
import geogebra.kernel.algos.AlgoDependentListExpression;
import geogebra.kernel.algos.AlgoDotPlot;
import geogebra.kernel.algos.AlgoElement;
import geogebra.kernel.algos.AlgoFunctionAreaSums;
import geogebra.kernel.algos.AlgoHistogram;
import geogebra.kernel.algos.AlgoListElement;
import geogebra.kernel.algos.AlgoListMax;
import geogebra.kernel.algos.AlgoListMin;
import geogebra.kernel.algos.AlgoNormalQuantilePlot;
import geogebra.kernel.algos.AlgoPolyLine;
import geogebra.kernel.algos.AlgoResidualPlot;
import geogebra.kernel.algos.AlgoStemPlot;
import geogebra.kernel.algos.AlgoText;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoText;
import geogebra.kernel.statistics.AlgoFitExp;
import geogebra.kernel.statistics.AlgoFitLineY;
import geogebra.kernel.statistics.AlgoFitLog;
import geogebra.kernel.statistics.AlgoFitLogistic;
import geogebra.kernel.statistics.AlgoFitPoly;
import geogebra.kernel.statistics.AlgoFitPow;
import geogebra.kernel.statistics.AlgoFitSin;
import geogebra.kernel.statistics.AlgoMean;
import geogebra.kernel.statistics.AlgoStandardDeviation;
import geogebra.main.Application;

import java.awt.Color;

/**
 * 
 * Creates geos for use in plot panels and provides updates to plot panel settings 
 * based on these geos.
 * 
 */
public class StatGeo   {

	private Application app;
	private Kernel kernel; 
	private Construction cons;

	private double xMinData, xMaxData, yMinData, yMaxData;		
	private double[] dataBounds;

	public static final int TABLE_ONE_VAR = 0;
	public static final int TABLE_TWO_VAR = 1;
	public static final int TABLE_REGRESSION = 2;

	private boolean histogramRight;
	private boolean removeFromConstruction = true;

	



	/*************************************************
	 * Constructs a GeoPlot instance
	 */
	public StatGeo(Application app){

		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();


	}



	public GeoElement redefineGeoFromString(GeoElement geo, String newValue){

		try {

			geo = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo, newValue, true, false);

			// set visibility
			geo.setEuclidianVisible(true);
			geo.setAuxiliaryObject(true);
			geo.setLabelVisible(false);

			return geo;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}*/



	//=================================================
	//       Plots and Updates
	//=================================================

	private void getDataBounds(GeoList dataList){
		getDataBounds( dataList, false, false);
	}
	private void getDataBounds(GeoList dataList, boolean isPointList){
		getDataBounds( dataList, true, false);
	}
	private void getDataBounds(GeoList dataList, boolean isPointList, boolean isMatrix){

		//String label = dataList.getLabel();
		dataBounds = new double[4];

		if(isMatrix){

			// create index for iterating through the lists in the matrix
			GeoNumeric index = new GeoNumeric(cons, 1);
			
			// create algo for extracting a list from the matrix that depends on index
			AlgoListElement le = new AlgoListElement(cons, dataList, index);
			removeFromConstructionList(le);
			
			// create list from this algo 
			GeoList list = (GeoList) le.getGeoElements()[0];
			
			// create algos to find the max and min values of the list
			AlgoListMax maxAlgo = new AlgoListMax(cons, list);
			AlgoListMin minAlgo = new AlgoListMin(cons, list);
			removeFromConstructionList(minAlgo);
			removeFromConstructionList(maxAlgo);

			// create max/min geos from the max/min algos
			GeoNumeric maxGeo = (GeoNumeric) maxAlgo.getGeoElements()[0];
			GeoNumeric minGeo = (GeoNumeric) minAlgo.getGeoElements()[0];

			// initialize the data bounds with max/min from the first list
			dataBounds[0] = ((GeoNumeric)minAlgo.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)maxAlgo.getGeoElements()[0]).getDouble();

			// iterate through the remaining lists to find the max/min for the matrix
			double min, max;
			for(int i = 1; i < dataList.size(); i++){
				
				index.setValue(i+1);  // use i+1 because Element[] uses 1-based counting
				index.updateCascade();
				min = minGeo.getDouble();
				max = maxGeo.getDouble();

				dataBounds[0] = Math.min(dataBounds[0], min);
				dataBounds[1] = Math.max(dataBounds[1], max);
			}
		}


		else if(isPointList){
			ExpressionNode enX = new ExpressionNode(kernel, dataList, Operation.XCOORD, null);
			ExpressionNode enY = new ExpressionNode(kernel, dataList, Operation.YCOORD, null);
			AlgoDependentListExpression listX = new AlgoDependentListExpression(cons, enX);
			AlgoDependentListExpression listY = new AlgoDependentListExpression(cons, enY);

			AlgoListMax maxX = new AlgoListMax(cons, (GeoList)listX.getGeoElements()[0]);
			AlgoListMax maxY = new AlgoListMax(cons, (GeoList)listY.getGeoElements()[0]);
			AlgoListMin minX = new AlgoListMin(cons, (GeoList)listX.getGeoElements()[0]);
			AlgoListMin minY = new AlgoListMin(cons, (GeoList)listY.getGeoElements()[0]);

			removeFromConstructionList(listX);
			removeFromConstructionList(listY);
			removeFromConstructionList(maxX);
			removeFromConstructionList(maxY);
			removeFromConstructionList(minX);
			removeFromConstructionList(minY);
			dataBounds[0] = ((GeoNumeric)minX.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)maxX.getGeoElements()[0]).getDouble();
			dataBounds[2] = ((GeoNumeric)minY.getGeoElements()[0]).getDouble();
			dataBounds[3] = ((GeoNumeric)maxY.getGeoElements()[0]).getDouble();

			//dataBounds[0] = this.evaluateExpression("Min[x(" + label + ")]");
			//dataBounds[1] = this.evaluateExpression("Max[x(" + label + ")]");
			//dataBounds[2] = this.evaluateExpression("Min[y(" + label + ")]");
			//dataBounds[3] = this.evaluateExpression("Max[y(" + label + ")]");
		}else{
			AlgoListMax max = new AlgoListMax(cons, dataList);
			AlgoListMin min = new AlgoListMin(cons, dataList);
			removeFromConstructionList(min);
			removeFromConstructionList(max);

			dataBounds[0] = ((GeoNumeric)min.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)max.getGeoElements()[0]).getDouble();
			//dataBounds[0] = this.evaluateExpression("Min[" + label + "]");
			//dataBounds[1] = this.evaluateExpression("Max[" + label + "]");
		}

		xMinData = dataBounds[0];
		xMaxData = dataBounds[1];
		yMinData = dataBounds[2];
		yMaxData = dataBounds[3];

	}



	public GeoElement createHistogram(GeoList dataList, int numClasses, StatPanelSettings settings, boolean isFrequencyPolygon){

		GeoElement geo;
		getDataBounds(dataList);
		double classWidth = (xMaxData - xMinData)/(numClasses); 
		histogramRight = !settings.isLeftRule;


		AlgoElement al, al2;
		if(settings.useManualClasses){
			classWidth = settings.classWidth;
			al = new AlgoClasses(cons, dataList, new GeoNumeric(cons, settings.classStart), new GeoNumeric(cons, settings.classWidth), null);
		}else{
			al = new AlgoClasses(cons, dataList, null, null, new GeoNumeric(cons, numClasses));
		}
		removeFromConstructionList(al);

		double density = -1;
		if(settings.frequencyType == StatPanelSettings.TYPE_RELATIVE)
			density = 1.0*classWidth/dataList.size();
		if(settings.frequencyType == StatPanelSettings.TYPE_NORMALIZED)
			density = 1.0/dataList.size();


		//if(isFrequencyPolygon)
		//	al2 = new AlgoFrequencyPolygon(cons, new GeoBoolean(cons, settings.isCumulative), 
		//		(GeoList)al.getGeoElements()[0], dataList, new GeoBoolean(cons, true), new GeoNumeric(cons, density));
		//else
		al2 = new AlgoHistogram(cons, new GeoBoolean(cons, settings.isCumulative), 
				(GeoList)al.getGeoElements()[0], dataList, new GeoBoolean(cons, true), new GeoNumeric(cons, density),histogramRight);
		removeFromConstructionList(al2);

		if(isFrequencyPolygon){
			AlgoPolyLine al3 = createFrequencyPolygon((AlgoHistogram) al2, settings.isCumulative);
			removeFromConstructionList(al3);
			geo = al3.getGeoElements()[0];
			geo.setObjColor(StatDialog.OVERLAY_COLOR);
			geo.setLineThickness(StatDialog.thicknessCurve);

		}else{
			geo = al2.getGeoElements()[0];
			geo.setObjColor(StatDialog.HISTOGRAM_COLOR);
			geo.setAlphaValue(StatDialog.opacityBarChart);
			geo.setLineThickness(StatDialog.thicknessBarChart);
		}
		return geo;	
	}


	/**
	 * Creates a FrequencyPolygon algo using AlgoPolyLine instead of AlgoFrequencyPolygon
	 * This is needed until FrequencyPolygonRight is implemented
	 * 
	 * @param histogram
	 * @param doCumulative
	 * @return
	 */
	private AlgoPolyLine createFrequencyPolygon(AlgoHistogram histogram, boolean doCumulative){

		double[] leftBorder = histogram.getLeftBorder();
		double yValue[] = histogram.getYValue();
		int size = doCumulative ? yValue.length : yValue.length-1;
		GeoPointND[] points = new GeoPoint[size];

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		if(doCumulative)
			points[0] = new GeoPoint(cons, null, leftBorder[0], 0.0, 1.0);
		for (int i = 0; i < yValue.length-1; i++) {
			if(doCumulative)
				points[i+1] = new GeoPoint(cons, null, leftBorder[i+1], yValue[i], 1.0);
			else
				points[i] = new GeoPoint(cons, null, (leftBorder[i+1] + leftBorder[i])/2, yValue[i], 1.0);
		}	
		cons.setSuppressLabelCreation(suppressLabelCreation);

		AlgoPolyLine polyLine = new AlgoPolyLine(cons, null, points);
		removeFromConstructionList(polyLine);
		return polyLine;
	}



	public GeoElement createNormalCurveOverlay(GeoList dataList){

		GeoElement geo;
		//String label = dataList.getLabel();	
		//String text = "Normal[Mean[" + label + "],SD[" + label + "],x]";

		AlgoMean mean = new AlgoMean(cons, dataList);
		AlgoStandardDeviation sd = new AlgoStandardDeviation(cons, dataList);

		removeFromConstructionList(mean);
		removeFromConstructionList(sd);

		GeoElement meanGeo = mean.getGeoElements()[0];
		GeoElement sdGeo = sd.getGeoElements()[0];

		FunctionVariable x = new FunctionVariable(kernel);

		ExpressionNode normal = new ExpressionNode(kernel, x, Operation.MINUS, meanGeo);
		normal = new ExpressionNode(kernel, normal, Operation.DIVIDE, sdGeo);
		normal = new ExpressionNode(kernel, normal, Operation.POWER, new MyDouble(kernel,2.0));
		normal = new ExpressionNode(kernel, normal, Operation.DIVIDE, new MyDouble(kernel,-2.0));
		normal = new ExpressionNode(kernel, normal, Operation.EXP, null);
		normal = new ExpressionNode(kernel, normal, Operation.DIVIDE, new MyDouble(kernel,Math.sqrt(2*Math.PI)));
		normal = new ExpressionNode(kernel, normal, Operation.DIVIDE, sdGeo);

		Function f = new Function(normal, x);		
		geo = new GeoFunction(cons, f);

		//Application.debug(text);
		//geo = createGeoFromString(text);
		geo.setObjColor(StatDialog.OVERLAY_COLOR);
		geo.setLineThickness(StatDialog.thicknessCurve);

		return geo;	
	}



	public void  getHistogramSettings(GeoList dataList, GeoElement histogram, StatPanelSettings settings){	

		getDataBounds(dataList);	

		double freqMax = ((AlgoFunctionAreaSums)histogram.getParentAlgorithm()).getFreqMax();
		//if(settings.type == StatPanelSettings.TYPE_RELATIVE)
		//freqMax = 1.0;

		if(settings.useManualClasses){
			double[] leftBorder = ((AlgoFunctionAreaSums)histogram.getParentAlgorithm()).getLeftBorder();
			xMinData = leftBorder[0];
			xMaxData = leftBorder[leftBorder.length-1];
		}

		yMinData = 0.0;
		yMaxData = freqMax;

		setXYBounds(settings, .2, .1);

		settings.showYAxis = true;
		settings.isEdgeAxis[0] = false;
		settings.isEdgeAxis[1] = true;
		settings.isPositiveOnly[1] = true;
		settings.forceXAxisBuffer = true;


	}


	public GeoElement createBoxPlot(GeoList dataList){

		//String label = dataList.getLabel();	
		GeoElement geo;

		//String	text = "BoxPlot[1,0.5," + label + "]";
		//geo  = createGeoFromString(text);

		AlgoBoxPlot boxPlot = new AlgoBoxPlot(cons, new MyDouble(kernel, 1d), new MyDouble(kernel, 0.5), dataList);
		removeFromConstructionList(boxPlot);
		geo = boxPlot.getGeoElements()[0];

		geo.setObjColor(StatDialog.BOXPLOT_COLOR);
		geo.setAlphaValue(StatDialog.opacityBarChart);
		return geo;		
	}

	public void getBoxPlotSettings(GeoList dataList, StatPanelSettings settings){

		getDataBounds(dataList);
		if(settings.isAutomaticWindow){
			double buffer = .25*(xMaxData - xMinData);
			settings.xMin = xMinData - buffer;
			settings.xMax = xMaxData + buffer;
			settings.yMin = -1.0;
			settings.yMax = 2;
		}

		settings.showYAxis = false;
		settings.forceXAxisBuffer = true;

	}

	public GeoElement[] createMultipleBoxPlot(GeoList dataList){

		//String label = dataList.getLabel();	
		GeoElement geo;

		// Sequence[BoxPlot[k, 0.33333, Element[mm, k]], k, 1, Length[mm]]
		//String len = "Length[" + label + "]";
		//String	text = "Sequence[BoxPlot[k, 1/3, Element[" + label + "," + len + "-k+1]], k, 1," + len + "]";
		//geo  = createGeoFromString(text);

		//AlgoListLength len = new AlgoListLength(cons, dataList);
		//GeoNumeric num = (GeoNumeric) len.getGeoElements()[0];
		//removeFromConstructionList(len);

		int length = dataList.size();

		GeoElement[] ret = new GeoElement[length];

		for (int i = 0 ; i < length ; i++) {
			AlgoBoxPlot bp = new AlgoBoxPlot(cons, new GeoNumeric(cons, i+1), new GeoNumeric(cons, 1d/3d), (GeoList)dataList.get((length-1)-i));
			cons.removeFromAlgorithmList(bp);
			ret[i] = bp.getGeoElements()[0];
			ret[i].setObjColor(StatDialog.BOXPLOT_COLOR);
			ret[i].setAlphaValue(StatDialog.opacityBarChart);

		}

		return ret;		
	}

	public void getMultipleBoxPlotSettings(GeoList dataList, StatPanelSettings settings){
		if(settings.isAutomaticWindow){
			getDataBounds(dataList, false,true);		
			double buffer = .25*(xMaxData - xMinData);
			settings.xMin = xMinData - buffer;
			settings.xMax = xMaxData + buffer;
			settings.yMin = -1.0;
			settings.yMax = dataList.size()+1;
		}
		settings.showYAxis = false;
		settings.forceXAxisBuffer = true;
	}


	public GeoElement[] createBoxPlotTitles(StatDialog statDialog, StatPanelSettings settings){

		String[] dataTitles = statDialog.getDataTitles();	

		int length = dataTitles.length;
		GeoElement[] ret = new GeoElement[length];

		for (int i = 0; i < dataTitles.length; i++){
			GeoPoint p = new GeoPoint(cons, settings.xMin, i+1d, 1d);
			GeoText t = new GeoText(cons, "  "+dataTitles[dataTitles.length - i - 1]);
			AlgoText text = new AlgoText(cons, t, p, null, null);
			cons.removeFromAlgorithmList(text);
			ret[i] = text.getGeoElements()[0];
			ret[i].setBackgroundColor(Color.WHITE);
			ret[i].setObjColor(Color.BLACK);
		}
		return ret;		
	}


	public GeoElement createDotPlot(GeoList dataList){

		//String label = dataList.getLabel();	
		//GeoElement geo;

		//String text = "DotPlot[" + label + "]";
		//geo  = createGeoFromString(text);

		AlgoDotPlot dp = new AlgoDotPlot(cons, dataList);
		removeFromConstructionList(dp);
		GeoElement geo = dp.getGeoElements()[0];

		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(StatDialog.opacityBarChart);

		return geo;	
	}


	public void updateDotPlot(GeoList dataList, GeoElement dotPlot, StatPanelSettings settings){

		getDataBounds(dataList);

		if(settings.isAutomaticWindow){
			double buffer = .25*(xMaxData - xMinData);		
			settings.xMin = xMinData - buffer;
			settings.xMax = xMaxData + buffer;
			settings.yMin = -1.0;

			ExpressionNode en = new ExpressionNode(kernel, dotPlot, Operation.YCOORD, null);
			AlgoDependentListExpression list = new AlgoDependentListExpression(cons, en);
			AlgoListMax max = new AlgoListMax(cons, (GeoList)list.getGeoElements()[0]);

			removeFromConstructionList(list);
			removeFromConstructionList(max);

			settings.yMax = ((GeoNumeric)max.getGeoElements()[0]).getDouble() + 1;
		}

		settings.showYAxis = false;
		settings.forceXAxisBuffer = true;

	}


	public GeoElement createNormalQuantilePlot(GeoList dataList){

		//String label = dataList.getLabel();	
		//GeoElement geo;

		//String text = "NormalQuantilePlot[" + label + "]";
		//geo  = createGeoFromString(text);

		AlgoNormalQuantilePlot qp = new AlgoNormalQuantilePlot(cons, dataList);
		removeFromConstructionList(qp);
		GeoElement geo = qp.getGeoElements()[0];		

		geo.setObjColor(StatDialog.NQPLOT_COLOR);
		geo.setAlphaValue(StatDialog.opacityBarChart);
		geo.setLineThickness(StatDialog.thicknessCurve);

		return geo;	
	}


	public void updateNormalQuantilePlot(GeoList dataList, StatPanelSettings settings){

		getDataBounds(dataList);
		if(settings.isAutomaticWindow){
			double buffer = .25*(xMaxData - xMinData);		
			settings.xMin = xMinData - buffer;
			settings.xMax = xMaxData + buffer;
			settings.yMin = -4.0;
			settings.yMax = 4.0;
			settings.showYAxis = true;
		}

		settings.isEdgeAxis[1] = true;
		settings.forceXAxisBuffer = false;
		settings.isPositiveOnly[0] = false;
		settings.isPositiveOnly[1] = false;	
	}




	public GeoElement createScatterPlotLine(GeoList points){

		AlgoPolyLine polyLine = new AlgoPolyLine(cons, null, points);
		removeFromConstructionList(polyLine);
		GeoElement geo = polyLine.getGeoElements()[0];

		// set visibility
		geo.setEuclidianVisible(true);	
		geo.setAuxiliaryObject(true);
		geo.setLabelVisible(false);	
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(StatDialog.opacityBarChart);

		return geo;


	}



	public GeoElement createScatterPlot(GeoList dataList){

		// copy the dataList geo
		GeoList geo = new GeoList(cons);
		geo.setAuxiliaryObject(true);

		for(int i=0; i<dataList.size(); ++i)
			geo.add(dataList.get(i));	

		// set visibility
		geo.setEuclidianVisible(true);	
		geo.setAuxiliaryObject(true);
		geo.setLabelVisible(false);	
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(StatDialog.opacityBarChart);

		return geo;
	}

	public void getScatterPlotSettings(GeoList dataList, StatPanelSettings settings){

		getDataBounds(dataList, true);	

		setXYBounds(settings);

		settings.showYAxis = true;
		settings.forceXAxisBuffer = false;
		settings.isEdgeAxis[0] = true;
		settings.isEdgeAxis[1] = true;
		settings.isPositiveOnly[0] = true;
		settings.isPositiveOnly[1] = true;		

	}



	public GeoElement createRegressionPlot(GeoList dataList, int regType, int order, boolean residual){

		boolean regNone = false;

		AlgoElement algo;

		switch (regType) {
		case StatDialog.REG_LOG:
			algo = new AlgoFitLog(cons, dataList);
			break;
		case StatDialog.REG_POLY:
			algo = new AlgoFitPoly(cons, dataList, new MyDouble(kernel, order));
			break;
		case StatDialog.REG_POW:
			algo = new AlgoFitPow(cons, dataList);
			break;
		case StatDialog.REG_EXP:
			algo = new AlgoFitExp(cons, dataList);
			break;
		case StatDialog.REG_SIN:
			algo = new AlgoFitSin(cons, dataList);
			break;
		case StatDialog.REG_LOGISTIC:
			algo = new AlgoFitLogistic(cons, dataList);
			break;
		case StatDialog.REG_NONE:
			regNone = true;
			// fall through to linear
		case StatDialog.REG_LINEAR:
		default:
			algo = new AlgoFitLineY(cons, dataList);
			break;

		}

		removeFromConstructionList(algo);
		GeoElement geo = algo.getGeoElements()[0];

		if (residual) {
			AlgoResidualPlot algoRP = new AlgoResidualPlot(cons, dataList, (GeoFunctionable) geo);
			geo = algoRP.getGeoElements()[0];
			geo.setObjColor(StatDialog.DOTPLOT_COLOR);
			geo.setAlphaValue(StatDialog.opacityBarChart);
			geo.setLineThickness(StatDialog.thicknessCurve);
		} else {

			// set geo options
			geo.setObjColor(StatDialog.REGRESSION_COLOR);
			if(regType == StatDialog.REG_LINEAR)	
				((GeoLine)geo).setToExplicit();	

			// hide the dummy geo
			if(regNone) geo.setEuclidianVisible(false);
		}

		return geo;

	}




	public void updateRegressionPlot(GeoList dataList, StatPanelSettings settings){

		if(settings.isAutomaticWindow){
			getDataBounds(dataList, true);

			double xBuffer = .25*(xMaxData - xMinData);
			settings.xMin = xMinData - xBuffer;
			settings.xMax = xMaxData + xBuffer;

			double yBuffer = .25*(yMaxData - yMinData);
			settings.yMin = yMinData - yBuffer;
			settings.yMax = yMaxData + yBuffer;
		}

		settings.showYAxis = true;
		settings.forceXAxisBuffer = false;

	}
	/*
	public GeoElement createResidualPlot(GeoList dataList, int regType, int order){

		GeoElement geo = null;

		if (regType == StatDialog.REG_NONE){
			return new GeoList(cons);
		}

		String label = dataList.getLabel();	

		String regFcn = regCmd[regType] + "[" + label + "]";
		if(regType == StatDialog.REG_POLY)
			regFcn = regCmd[regType] + "[" + label + "," + order + "]";

		String text = "ResidualPlot[" + label + "," + regFcn + "]";
		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;

	}*/

	public void getResidualPlotSettings(GeoList dataList, GeoElement residualPlot, StatPanelSettings settings){

		getDataBounds(dataList, true);	

		double[] residualBounds = ((AlgoResidualPlot)residualPlot.getParentAlgorithm()).getResidualBounds();
		yMaxData = Math.max(Math.abs(residualBounds[0]),Math.abs(residualBounds[1]));
		yMinData = -yMaxData;

		setXYBounds(settings);

		settings.showYAxis = true;
		settings.forceXAxisBuffer = false;
		settings.isEdgeAxis[0] = false;
		settings.isEdgeAxis[1] = true;
		settings.isPositiveOnly[0] = true;
		settings.isPositiveOnly[1] = false;

	}

	private void setXYBounds(StatPanelSettings settings){
		setXYBounds(settings, .2, .2);
	}

	private void setXYBounds(StatPanelSettings settings, double xBufferScale, double yBufferScale){

		if(settings.isAutomaticWindow){

			double xBuffer = xBufferScale*(xMaxData - xMinData);
			settings.xMin = xMinData - xBuffer;
			settings.xMax = xMaxData + xBuffer;

			double yBuffer = yBufferScale*(yMaxData - yMinData);
			settings.yMin = yMinData - yBuffer;
			settings.yMax = yMaxData + yBuffer;
		}

	}


	public String getStemPlotLatex(GeoList dataList, int adjustment){

		//String label = dataList.getLabel();	

		//String	text = "StemPlot[" + label + "," + adjustment + "]";
		//tempGeo  = createGeoFromString(text);

		AlgoStemPlot sp = new AlgoStemPlot(cons, dataList, new GeoNumeric(cons, adjustment));
		GeoElement tempGeo = sp.getGeoElements()[0];
		removeFromConstructionList(sp);

		String latex = tempGeo.getLaTeXdescription();
		tempGeo.remove();

		return latex;		
	}

	
	
	
	public boolean doRemoveFromConstruction() {
		return removeFromConstruction;
	}
	public void setRemoveFromConstruction(boolean removeFromConstruction) {
		this.removeFromConstruction = removeFromConstruction;
	}
	
	private void removeFromConstructionList(ConstructionElement ce){
		if(removeFromConstruction)
			cons.removeFromConstructionList(ce);
	}



}
