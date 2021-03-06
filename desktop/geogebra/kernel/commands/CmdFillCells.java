package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.algos.AlgoCellRange;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoLocus;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 *FillCells
 */
class CmdFillCells extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillCells(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			app.setScrollToShow(false);
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				GeoList cellRange = (GeoList) arg[0];

				if (!(cellRange.getParentAlgorithm() instanceof AlgoCellRange)) {
					Application.debug("not cell range");
					throw argErr(app, c.getName(), arg[0]);

				}

				AlgoCellRange algo = (AlgoCellRange) cellRange
						.getParentAlgorithm();

				Point[] points = algo.getRectangle();

				Point startCoords = points[0];
				Point endCoords = points[1];

				int minCol = Math.min(startCoords.x, endCoords.x);
				int maxCol = Math.max(startCoords.x, endCoords.x);
				int minRow = Math.min(startCoords.y, endCoords.y);
				int maxRow = Math.max(startCoords.y, endCoords.y);

				// Application.debug(minCol+" "+maxCol+" "+minRow+" "+maxRow);

				GeoElement geo = (GeoElement) arg[1];
				GeoElement[] ret = {};

				if (geo.isGeoLocus()) {

					if (!geo.isDefined())
						throw argErr(app, c.getName(), arg[1]);

					if (minCol + 1 != maxCol)
						throw argErr(app, c.getName(), arg[0]);

					GeoLocus locus = (GeoLocus) geo;

					ArrayList<MyPoint> al = locus.getMyPointList();

					int length = Math.min(al.size(), maxRow - minRow);

					for (int i = 0; i < length; i++) {
						int row = i + minRow;

						try {
							// cell will have been autocreated by eg A1:A3 in
							// command, so delete
							kernel.lookupLabel(
									GeoElement.getSpreadsheetCellName(minCol,
											row)).remove();
							kernel.lookupLabel(
									GeoElement.getSpreadsheetCellName(
											minCol + 1, row)).remove();

							MyPoint p = al.get(i);														

							GeoElement.setSpreadsheetCell(app, row, minCol,
									new GeoNumeric(cons, p.x));
							GeoElement.setSpreadsheetCell(app, row, minCol + 1,
									new GeoNumeric(cons, p.y));
						} catch (Exception e) {
							e.printStackTrace();
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), arg[1]);
						}

					}
					app.setScrollToShow(true);

					return ret;

				}
				if (!geo.isGeoList()) {
					for (int row = minRow; row <= maxRow; row++)
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								kernel.lookupLabel(
										GeoElement.getSpreadsheetCellName(col,
												row)).remove();

								GeoElement.setSpreadsheetCell(app, row, col,
										geo);
							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(app, c.getName(), arg[1]);
							}
						}
					app.setScrollToShow(true);
					return ret;
				}

				// TODO finish
				// GeoList list = (GeoList)geo;
				// if (list.isMatrix())

				app.storeUndoInfo();
				app.setScrollToShow(true);
				return ret;

			} else {

				if (GeoElement.isSpreadsheetLabel(arg[0].getLabel())) {

					if (!arg[1].isGeoList()) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[1]);
					}

					GeoList list = (GeoList) arg[1];

					Matcher matcher = GeoElement.spreadsheetPattern
							.matcher(arg[0].getLabel());
					int column = GeoElement.getSpreadsheetColumn(matcher);
					int row = GeoElement.getSpreadsheetRow(matcher);

					if (row == -1 || column == -1) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[0]);
					}

					if (list.isMatrix()) {
						// 2D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						try {
							int rows = list.size();
							int cols = ((GeoList) list.get(0)).size();
							for (int r = 0; r < rows; r++) {
								GeoList rowList = (GeoList) list.get(r);
								for (int c1 = 0; c1 < cols; c1++) {
									GeoElement
											.setSpreadsheetCell(app, row + r,
													column + c1, rowList
															.get(c1).copy());
								}
							}
						} catch (Exception e) {
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), list);
						}

					} else {
						// 1D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						for (int i = list.size() - 1; i >= 0; i--)
							try {
								// Application.debug("setting "+row+" "+(column+i)+" to "+list.get(i).toString());
								GeoElement.setSpreadsheetCell(app, row, column
										+ i, list.get(i).copy());
							} catch (Exception e) {
								e.printStackTrace();
								app.setScrollToShow(true);
								throw argErr(app, c.getName(), arg[1]);
							}
					}

				} else {
					app.setScrollToShow(true);
					throw argErr(app, c.getName(), arg[0]);
				}
			}

			GeoElement[] ret = {};
			app.storeUndoInfo();
			app.setScrollToShow(true);
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
