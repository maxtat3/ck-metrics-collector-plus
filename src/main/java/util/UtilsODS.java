package util;

import com.github.miachm.sods.Sheet;

public class UtilsODS {

//	===========================================
//		Util ODS sheet methods
//	===========================================
	/**
	 * Simple ODS util method for converting given row and column to A1 notation.
	 * A1 notation is a string representation of a subset in a Spreadsheet where
	 * the column is represented by a capital letter (starting in A)
	 * and the row by a row number (starting in 1).
	 *
	 * Examples:
	 * row=1, column=A, returned cell value "A1"
	 * row=5, column=C, returned cell value "C5"
	 *
	 * @param row row in ODS sheet started traditionally from 1.
	 * @param column column in ODS sheet started traditionally from A
	 * @return cell value in A1 notation representation
	 * @see {@link Sheet#getRange(String)}
	 */
	public static String convToA1NotationCell(String column, int row) {
		isA1Notation(row, column);
		return column + row;
	}

	public static String convToA1NotationCell(int row, String column) {
		return convToA1NotationCell(column, row);
	}

	// TODO: 12.03.25 need javadoc
	public static String convToA1NotationRange(String startColumn, int startRow, String endColumn, int endRow) {
		isA1Notation(startRow, startColumn);
		isA1Notation(endRow, endColumn);
		return startColumn + startRow + ":" + endColumn + endRow;
	}

	public static String convToA1NotationRange(int startRow, String startColumn, int endRow, String endColumn) {
		return convToA1NotationRange(startColumn, startRow, endColumn, endRow);
	}

	private static void isA1Notation(int row, String column) {
		if (row == 0) throw new IllegalArgumentException("Row value must be non zero and started from 1. Given=" + row);
		if (row < 0) throw new IllegalArgumentException("Row value must be positive. Given=" + row);

		// Check is column contained only letters A-Z.
		boolean correctColumnFlag = true;
		for(int i = 0; i < column.length(); i++) {
			char ch = column.charAt(i);
			if (!Character.isLetter(ch) && !Character.isTitleCase(ch)) {
				correctColumnFlag = false;
				break;
			}
		}
		if(!correctColumnFlag)	throw new IllegalArgumentException("Column value must be contains only letters A-Z in title case. Given=" + column);
	}
//	===========================================

}
