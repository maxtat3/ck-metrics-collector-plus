package util;

import app.ODSProcess;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsODSTest {

//  +++
//	===========================================
//      convToA1NotationCell
//	===========================================
	@Test
	public void testConvToA1Notation_cell_A1Cell() {
		String cell = UtilsODS.convToA1NotationCell(1, "A");
		Assert.assertEquals("A1", cell);
	}

	@Test
	public void testConvToA1Notation_Cell_cell_C15() {
		String cell = UtilsODS.convToA1NotationCell(15, "C");
		Assert.assertEquals("C15", cell);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1Notation_Cell_cell_zeroRow() {
		UtilsODS.convToA1NotationCell(0, "A");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1Notation_Cell_cell_negRow() {
		UtilsODS.convToA1NotationCell(-5, "A");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1Notation_Cell_cell_NotLetterColumn_1() {
		UtilsODS.convToA1NotationCell(1, "@");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1Notation_Cell_cell_NotLetterColumn_2() {
		UtilsODS.convToA1NotationCell(1, "/");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1Notation_Cell_cell_NotLetterColumn_3() {
		UtilsODS.convToA1NotationCell(1, "A&");
	}
//	===========================================

	//	+++
//	===========================================
//      convToA1NotationRange
//	===========================================
	@Test
	public void testConvToA1NotationRange_range_A1toE1() {
		String range = UtilsODS.convToA1NotationRange("A", 1, "E", 1);
		Assert.assertEquals("A1:E1", range);
	}

	@Test
	public void testConvToA1NotationRange_range_G5toL17() {
		String range = UtilsODS.convToA1NotationRange("G", 5, "L", 15);
		Assert.assertEquals("G5:L15", range);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1NotationRange_range_zeroStartRow() {
		UtilsODS.convToA1NotationRange("A", 0, "E", 1);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1NotationRange_range_zeroStartAndEndRow() {
		UtilsODS.convToA1NotationRange("A", 0, "E", 0);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1NotationRange_range_NotLetterStartColumn() {
		UtilsODS.convToA1NotationRange("@", 1, "E", 7);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1NotationRange_range_NotLetterStartAndEndColumn_1() {
		UtilsODS.convToA1NotationRange("&", 3, "!", 7);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvToA1NotationRange_range_NotLetterStartAndEndColumn_2() {
		UtilsODS.convToA1NotationRange("A&", 1, "E*", 5);
	}
//	===========================================

}