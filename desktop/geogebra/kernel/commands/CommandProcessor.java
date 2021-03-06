/**
 * GeoGebra - Dynamic Mathematics for Everyone 
 * http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 */

package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.main.MyError;
import geogebra.common.util.Unicode;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.MySpecialDouble;
import geogebra.kernel.arithmetic.Variable;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.main.Application;

import java.util.ArrayList;

/**
 * Resolves arguments of the command, checks their validity and creates
 * resulting geos via appropriate Kernel methods
 */
public abstract class CommandProcessor {

	/** application */
	protected Application app;
	/** kernel */
	protected Kernel kernel;
	/** construction */
	Construction cons;
	private AlgebraProcessor algProcessor;

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CommandProcessor(Kernel kernel) {
		this.kernel = kernel;
		cons = kernel.getConstruction();
		app = kernel.getApplication();
		algProcessor = kernel.getAlgebraProcessor();
	}

	/**
	 * Every CommandProcessor has to implement this method
	 * 
	 * @param c
	 *            command
	 * @return list of resulting geos
	 * @throws MyError
	 * @throws CircularDefinitionException
	 */
	public abstract GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException;

	/**
	 * Resolves arguments. When argument produces mor geos, only first is taken.
	 * 
	 * @param c
	 * @return array of arguments
	 * @throws MyError
	 */
	protected final GeoElement[] resArgs(Command c) throws MyError {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		GeoElement[] result = new GeoElement[arg.length];

		for (int i = 0; i < arg.length; ++i) {
			// resolve variables in argument expression
			arg[i].resolveVariables();

			// resolve i-th argument and get GeoElements
			// use only first resolved argument object for result
			result[i] = resArg(arg[i])[0];
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return result;
	}

	/**
	 * Resolves argument
	 * 
	 * @param arg
	 * @return array of arguments
	 * @throws MyError
	 */
	final GeoElement[] resArg(ExpressionNode arg) throws MyError {
		GeoElement[] geos = algProcessor.processExpressionNode(arg);

		if (geos != null)
			return geos;
		else {
			String[] str = { "IllegalArgument", arg.toString() };
			throw new MyError(app, str);
		}
	}

	/**
	 * Resolve arguments of a command that has a local numeric variable at the
	 * position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
	 * @param varPos
	 * @param initPos
	 * @return Array of arguments
	 */
	protected final GeoElement[] resArgsLocalNumVar(Command c, int varPos,
			int initPos) {
		// check if there is a local variable in arguments
		String localVarName = c.getVariableName(varPos);
		if (localVarName == null) {
			throw argErr(app, c.getName(), c.getArgument(varPos));
		}
		// imaginary unit as local variable name
		else if (localVarName.equals(Unicode.IMAGINARY)) {
			// replace all imaginary unit objects in command arguments by a variable "i"object
			localVarName = "i";
			Variable localVar = new Variable(kernel, localVarName);
			c.replace(kernel.getImaginaryUnit(), localVar);			
		}
		// Euler constant as local variable name
		else if (localVarName.equals(Unicode.EULER_STRING)) {
			// replace all imaginary unit objects in command arguments by a variable "i"object
			localVarName = "e";
			Variable localVar = new Variable(kernel, localVarName);
			c.replace(MySpecialDouble.getEulerConstant(kernel), localVar);
		}

		// add local variable name to construction
		Construction cmdCons = (Construction) c.getKernel().getConstruction();
		GeoNumeric num = new GeoNumeric(cmdCons);
		cmdCons.addLocalVariable(localVarName, num);

		// initialize first value of local numeric variable from initPos
		if (initPos != varPos) {
			boolean oldval = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			NumberValue initValue = (NumberValue) resArg(c.getArgument(initPos))[0];
			cons.setSuppressLabelCreation(oldval);
			num.setValue(initValue.getDouble());
		}

		// set local variable as our varPos argument
		c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));

		// resolve all command arguments including the local variable just
		// created
		GeoElement[] arg = resArgs(c);

		// remove local variable name from kernel again
		cmdCons.removeLocalVariable(localVarName);
		return arg;
	}

	/**
	 * Resolve arguments of a command that has a several local numeric variable
	 * at the position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
	 * @param varPos
	 *            positions of local variables
	 * @param initPos
	 *            positions of vars to be initialized
	 * @return array of arguments
	 */
	protected final GeoElement[] resArgsLocalNumVar(Command c, int varPos[],
			int initPos[]) {

		String[] localVarName = new String[varPos.length];

		for (int i = 0; i < varPos.length; i++) {
			// check if there is a local variable in arguments
			localVarName[i] = c.getVariableName(varPos[i]);
			if (localVarName[i] == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos[i]));
			}
		}

		// add local variable name to construction
		Construction cmdCons = (Construction) c.getKernel().getConstruction();
		GeoNumeric[] num = new GeoNumeric[varPos.length];
		for (int i = 0; i < varPos.length; i++) {
			num[i] = new GeoNumeric(cmdCons);
			cmdCons.addLocalVariable(localVarName[i], num[i]);
		}

		// initialize first value of local numeric variable from initPos
		boolean oldval = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		NumberValue[] initValue = new NumberValue[varPos.length];
		for (int i = 0; i < varPos.length; i++)
			initValue[i] = (NumberValue) resArg(c.getArgument(initPos[i]))[0];
		cons.setSuppressLabelCreation(oldval);
		for (int i = 0; i < varPos.length; i++)
			num[i].setValue(initValue[i].getDouble());

		// set local variable as our varPos argument
		for (int i = 0; i < varPos.length; i++)
			c.setArgument(varPos[i], new ExpressionNode(c.getKernel(), num[i]));

		// resolve all command arguments including the local variable just
		// created
		GeoElement[] arg = resArgs(c);

		// remove local variable name from kernel again
		for (int i = 0; i < varPos.length; i++)
			cmdCons.removeLocalVariable(localVarName[i]);
		return arg;
	}

	private StringBuilder sb;

	/**
	 * Creates wrong argument error
	 * 
	 * @param app
	 * @param cmd
	 * @param arg
	 * @return wrong argument error
	 */
	protected final MyError argErr(Application app, String cmd, Object arg) {
		String localName = app.getCommand(cmd);
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		sb.append(app.getCommand("Command"));
		sb.append(' ');
		sb.append(localName);
		sb.append(":\n");
		sb.append(app.getError("IllegalArgument"));
		sb.append(": ");
		if (arg instanceof GeoElement)
			sb.append(((GeoElement) arg).getNameDescription());
		else if (arg != null)
			sb.append(arg.toString());
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(app.getCommandSyntax(cmd));
		return new MyError(app, sb.toString(), cmd);
	}

	/**
	 * Creates wrong parameter count error
	 * 
	 * @param app
	 * @param cmd
	 * @param argNumber
	 *            (-1 for just show syntax)
	 * @return wrong parameter count error
	 */
	protected final MyError argNumErr(Application app, String cmd, int argNumber) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		getCommandSyntax(sb, app, cmd, argNumber);
		return new MyError(app, sb.toString(), cmd);
	}

	/**
	 * Copies error syntax into a StringBuilder
	 * 
	 * @param sb
	 * @param app
	 * @param cmd
	 * @param argNumber
	 *            (-1 for just show syntax)
	 */
	public static void getCommandSyntax(StringBuilder sb, Application app,
			String cmd, int argNumber) {
		sb.append(app.getCommand("Command"));
		sb.append(' ');
		sb.append(app.getCommand(cmd));
		if (argNumber > -1) {
			sb.append(":\n");
			sb.append(app.getError("IllegalArgumentNumber"));
			sb.append(": ");
			sb.append(argNumber);
		}
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(app.getCommandSyntax(cmd));

	}

	/**
	 * Creates change dependent error
	 * 
	 * @param app
	 * @param geo
	 * @return change dependent error
	 */
	final static MyError chDepErr(Application app, GeoElement geo) {
		String[] strs = { "ChangeDependent", geo.getLongDescription() };
		return new MyError(app, strs);
	}

	/**
	 * Returns bad argument (according to ok array) and throws error if no was
	 * found.
	 * 
	 * @param ok
	 * @param arg
	 * @return bad argument
	 */
	protected static GeoElement getBadArg(boolean[] ok, GeoElement[] arg) {
		for (int i = 0; i < ok.length; i++) {
			if (!ok[i])
				return arg[i];
		}
		throw new Error("no bad arg");
	}

	/**
	 * Creates a dependent list with all GeoElement objects from the given
	 * array.
	 * 
	 * @param args
	 * @param type
	 *            -1 for any GeoElement object type; GeoElement.GEO_CLASS_ANGLE,
	 *            etc. for specific types
	 * @return null if GeoElement objects did not have the correct type
	 * @author Markus Hohenwarter
	 * @param kernel
	 * @param length
	 * @date Jan 26, 2008
	 */
	public static GeoList wrapInList(Kernel kernel, GeoElement[] args,
			int length, GeoClass type) {
		Construction cons = kernel.getConstruction();
		boolean correctType = true;
		ArrayList<GeoElement> geoElementList = new ArrayList<GeoElement>();
		for (int i = 0; i < length; i++) {
			if (type != GeoClass.DEFAULT || args[i].getGeoClassType() == type)
				geoElementList.add(args[i]);
			else {
				correctType = false;
				break;
			}
		}

		GeoList list = null;
		if (correctType) {
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			list = kernel.List(null, geoElementList, false);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		// list of zero size is not wanted
		if (list != null && list.size() == 0)
			list = null;

		return list;
	}
}
