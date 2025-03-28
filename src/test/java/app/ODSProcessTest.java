package app;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import exception.WrongProjectCellFormatException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pojo.Project;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ODSProcessTest {

	// Test ODS file contain 15 projects: rows 2...16
	private static final String SRC_PROJECTS_LIST = "src/test/resources/projects-list.ods";
	private static final String SHEET_FILE_MANAGERS = "FileManagers";

	private static final String SRC_ODS_PROJECTS_LIST_WRONG_FORMATS = "src/test/resources/projects-list-wrong-formats.ods";

//	+++
//	===========================================
//		getProjectsFromSheet
//	===========================================
	@Test
	public void testGetProjectsFromSheet_project1() {
		ODSProcess odsProcess = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS);
		ArrayList<Project> projects = odsProcess.getProjectsFromSheet();

		Project pr3 = projects.get(0);
		Assert.assertEquals(1, pr3.getNum());
		Assert.assertEquals("FM_J_01_AnExplorer", pr3.getId());
		Assert.assertEquals("AnExplorer", pr3.getName());
		Assert.assertEquals("FM", pr3.getType());
		Assert.assertEquals("", pr3.getSubType());
		Assert.assertEquals("J", pr3.getLanguage());
		Assert.assertEquals("https://github.com/1hakr/AnExplorer", pr3.getURL());
	}

	@Test
	public void testGetProjectsFromSheet_project8() {
		ODSProcess odsProcess = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS);
		ArrayList<Project> projects = odsProcess.getProjectsFromSheet();

		Project pr8 = projects.get(7);
		Assert.assertEquals(8, pr8.getNum());
		Assert.assertEquals("FM_K_08_Simple-File-Manager", pr8.getId());
		Assert.assertEquals("Simple File Manager", pr8.getName());
		Assert.assertEquals("FM", pr8.getType());
		Assert.assertEquals("", pr8.getSubType());
		Assert.assertEquals("K", pr8.getLanguage());
		Assert.assertEquals("https://github.com/SimpleMobileTools/Simple-File-Manager", pr8.getURL());
	}

	@Test
	public void testGetProjectsFromSheet_project15() {
		ODSProcess odsProcess = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS);
		ArrayList<Project> projects = odsProcess.getProjectsFromSheet();

		Project pr15 = projects.get(14);
		Assert.assertEquals(15, pr15.getNum());
		Assert.assertEquals("FM_FCH_K_15_File-Picker-Library", pr15.getId());
		Assert.assertEquals("File Picker Library", pr15.getName());
		Assert.assertEquals("FM", pr15.getType());
		Assert.assertEquals("FCH", pr15.getSubType());
		Assert.assertEquals("K", pr15.getLanguage());
		Assert.assertEquals("https://github.com/ChochaNaresh/FilePicker", pr15.getURL());
	}

	@Test
	public void testGetProjectsFromSheet_rangeFrom7To11() {
		// get rows range from 8 to 12. It's projects from 7 to 11
		ODSProcess odsProcess = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, new int[]{8, 12});
		ArrayList<Project> projects = odsProcess.getProjectsFromSheet();

		Assert.assertEquals(5, projects.size());

		// top bound (start) = 7
		Project pr7 = projects.get(0);
		Assert.assertEquals(7, pr7.getNum());
		Assert.assertEquals("FM_J_07_Dir", pr7.getId());
		Assert.assertEquals("FM", pr7.getType());
		Assert.assertEquals("https://github.com/veniosg/Dir/", pr7.getURL());

		// project in middle of selection range
		Project pr9 = projects.get(2);
		Assert.assertEquals(9, pr9.getNum());
		Assert.assertEquals("FM_K_09_fixate5", pr9.getId());
		Assert.assertEquals("FM", pr9.getType());
		Assert.assertEquals("https://github.com/aklinker1/fixate5/", pr9.getURL());

		// bottom bound (end) = 11
		Project pr11 = projects.get(4);
		Assert.assertEquals(11, pr11.getNum());
		Assert.assertEquals("FM_FCH_J_11_Android-Simple-File-Explorer-Library", pr11.getId());
		Assert.assertEquals("FM", pr11.getType());
		Assert.assertEquals("FCH", pr11.getSubType());
		Assert.assertEquals("https://github.com/BalioFVFX/Android-Simple-File-Explorer-Library", pr11.getURL());
	}

	@Test
	public void testGetProjectsFromSheet_rangeFrom10To15() {
 		// get rows range from 11 to 16. It's projects from 10 to 15
		ArrayList<Project> projects = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, new int[]{11, 16}).getProjectsFromSheet();

		Assert.assertEquals(6, projects.size());

		// top project bound (start) = 10
		Project pr10 = projects.get(0);
		Assert.assertEquals(10, pr10.getNum());
		Assert.assertEquals("FM_K_10_file-explorer", pr10.getId());
		Assert.assertEquals("FM", pr10.getType());
		Assert.assertEquals("https://github.com/Timur-Cheryapov/file-explorer/", pr10.getURL());

		// project in middle of selection range
		Project pr13 = projects.get(3);
		Assert.assertEquals(13, pr13.getNum());
		Assert.assertEquals("FM_FCH_K_13_AndroidFilePicker", pr13.getId());
		Assert.assertEquals("FM", pr13.getType());
		Assert.assertEquals("FCH", pr13.getSubType());
		Assert.assertEquals("https://github.com/MajidArabi/AndroidFilePicker", pr13.getURL());

		// bottom project bound (end) = 15
		Project pr15 = projects.get(5);
		Assert.assertEquals(15, pr15.getNum());
		Assert.assertEquals("FM_FCH_K_15_File-Picker-Library", pr15.getId());
		Assert.assertEquals("FM", pr15.getType());
		Assert.assertEquals("FCH", pr15.getSubType());
		Assert.assertEquals("https://github.com/ChochaNaresh/FilePicker", pr15.getURL());
	}
//	===========================================

//	+++
//	===========================================
//	    getRealLastProjectsRow
//	===========================================
	@Test
	public void testGetRealLastProjectsRow_sheetFM_15ProjectsFromSheet() {
		int lastProjectsRow = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS).getRealLastProjectsRowFromSheet();
		Assert.assertEquals(16, lastProjectsRow);
	}

	// TODO: 18.03.25 Sheet2 mv to const
	@Test
	public void testGetRealLastProjectsRow_sheet2_3ProjectsFromSheet() {
		int lastProjectsRow = new ODSProcess(SRC_PROJECTS_LIST, "Sheet2", Args.DEF_RANGE_PROC_ALL_ROWS).getRealLastProjectsRowFromSheet();
		Assert.assertEquals(4, lastProjectsRow);
	}
//	===========================================

//	===========================================
//      generateNamesInSheet
//	===========================================
//	+++
	/**
	 * Читаем вх. файл и для всех проектов и генерируем Names.
	 * Эти Names никуда не записываются, а возвращаются методом.
	 * Проверяются только Names трех проектов с номерами: 3, 9, 11.
	 */
	@Test
	public void testGenerateNamesInSheet() {
		Hashtable<Integer, String> names = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS).generateNamesInSheet();

		// By default, Hashtable not sorting in alphabet order.
		// Sorting can be done the list of keys extracted from the HashTable and retrieving values in that order.
		ArrayList<Integer> tmpList = Collections.list(names.keys());
		Collections.sort(tmpList);
		for (Integer row : tmpList) {
			String name = names.get(row);

//			Random check some projects.
//			Important note !
//			Number of rows in this SODS library started from 0. But in ODS Spreadsheet rows started from 1.
//			See {@link #START_ROW}
			if (row == 4) {
				Assert.assertEquals("PowerFileExplorer", name);

			} else if (row == 10) {
				Assert.assertEquals("fixate5", name);

			} else if (row == 12) {
					Assert.assertEquals("Android-Simple-File-Explorer-Library", name);
			}
		}
	}
//	===========================================

//	===========================================
//      generateIDsInSheet
//	===========================================
//	+++
	/**
	 * Читаем вх. файл и для всех проектов и генерируем ИД.
	 * Эти ИД никуда не записываются, а возвращаются методом.
	 * Проверяются только ИД трех проектов с номерами: 2, 8, 15.
	 */
	@Test
	public void testGenerateIDsInSheet() {
		Hashtable<Integer, String> ids = new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, Args.DEF_RANGE_PROC_ALL_ROWS).generateIDsInSheet();

		// By default, Hashtable not sorting in alphabet order.
		// Sorting can be done the list of keys extracted from the HashTable and retrieving values in that order.
		ArrayList<Integer> tmpList = Collections.list(ids.keys());
		Collections.sort(tmpList);
		for (Integer row : tmpList) {
			String id = ids.get(row);

//			Random check some projects.
//			Important note !
//			Number of rows in this SODS library started from 0. But in ODS Spreadsheet rows started from 1.
//			See {@link #START_ROW}
			if (row == 3) {
				Assert.assertEquals("FM_J_02_XFiles", id);

			} else if (row == 9) {
				Assert.assertEquals("FM_K_08_Simple-File-Manager", id);

			} else if (row == 16) {
				Assert.assertEquals("FM_FCH_K_15_File-Picker-Library", id);
			}
		}
	}
//	===========================================


//	===========================================
//      saveNamesToSheet
//	===========================================
// +++
	/**
	 * Предварительно созданные в этом тесте ИД сохраняем на лист в файл.
	 * Никакие проверки характеристик проектов не выполняются.
	 */
	@Test
	public void testSaveNamesToSheet() {
		final String sheetName = SHEET_FILE_MANAGERS;
		final String pr3name = "PowerFileExplorer", pr7name = "Dir", pr11name = "Android-Simple-File-Explorer-Library";
		final String pr3nameCell = "C4", pr7nameCell = "C8", pr11nameCell = "C12";

		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			Sheet sheet = book.getSheet(sheetName);

//			Clear previous Names in selected sheet before run test.
			sheet.getRange(pr3nameCell).setValue(null);    // clear value in sheet
			sheet.getRange(pr7nameCell).setValue(null);
			sheet.getRange(pr11nameCell).setValue(null);
			book.save(new File(SRC_PROJECTS_LIST));

//			// Save new Names in selected sheet in input source ODS file.
//			key - number of row in input source ODS file, value - generated Name of the project.
//			keys in this test case is not necessary.
			Hashtable<Integer, String> names = new Hashtable<>();
			names.put(4, pr3name);
			names.put(8, pr7name);
			names.put(12, pr11name);
			int count = new ODSProcess(SRC_PROJECTS_LIST, sheetName, Args.DEF_RANGE_PROC_ALL_ROWS, ODSProcess.ExcludeChecks.ID_NAME).saveNamesToSheet(names);

//			Again open selected sheet in input source ODS file.
			book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			sheet = book.getSheet(sheetName);

			Assert.assertEquals(3, count);
			Assert.assertEquals(pr3name, sheet.getRange(pr3nameCell).getValue().toString());
			Assert.assertEquals(pr7name, sheet.getRange(pr7nameCell).getValue().toString());
			Assert.assertEquals(pr11name, sheet.getRange(pr11nameCell).getValue().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
//	===========================================


//	===========================================
//      saveIDsToSheet
//	===========================================
// +++
	/**
	 * Предварительно созданные в этом тесте ИД сохраняем на лист в файл.
	 * Никакие проверки характеристик проектов не выполняются.
	 */
	@Test
	public void testSaveIDsToSheet() {
		final String sheetName = SHEET_FILE_MANAGERS;
		final String pr3name = "FM_J_03_PowerFileExplorer", pr7name = "FM_J_07_Dir", pr15name = "FM_FCH_K_15_File-Picker-Library";
		final String pr3idCell = "B4", pr7idCell = "B8", pr15idCell = "B16";

		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			Sheet sheet = book.getSheet(sheetName);

//			Clear previous IDs in selected sheet before run test.
			sheet.getRange(pr3idCell).setValue(null);    // clear value in sheet
			sheet.getRange(pr7idCell).setValue(null);
			sheet.getRange(pr15idCell).setValue(null);
			book.save(new File(SRC_PROJECTS_LIST));

//			// Save new IDs in selected sheet in input source ODS file.
//			key - number of row in input source ODS file, value - generated ID of the project.
//			keys in this test case is not necessary.
			Hashtable<Integer, String> ids = new Hashtable<>();
			ids.put(4, pr3name);
			ids.put(8, pr7name);
			ids.put(16, pr15name);
			int count = new ODSProcess(SRC_PROJECTS_LIST, sheetName, Args.DEF_RANGE_PROC_ALL_ROWS, ODSProcess.ExcludeChecks.ID).saveIDsToSheet(ids);

//			Again open selected sheet in input source ODS file.
			book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			sheet = book.getSheet(sheetName);

			Assert.assertEquals(3, count);
			Assert.assertEquals(pr3name, sheet.getRange(pr3idCell).getValue().toString());
			Assert.assertEquals(pr7name, sheet.getRange(pr7idCell).getValue().toString());
			Assert.assertEquals(pr15name, sheet.getRange(pr15idCell).getValue().toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
//	===========================================

//	===========================================
//      saveMetricsToSheet
//	===========================================
// +++
	/**
	 * <p> Created 4 project objects with some number value of metrics in certain rows
	 * 	in selected sheet in input source ODS file (further just a sheet, for simplicity).
	 * 	For each of these projects has ID which will be present in the sheet.
	 * 	This ID is used to map the project from the object to the sheet.
	 * 	If the object ID is not found on the sheet, the numeric values of metrics will not be written.
	 *
	 * 	<p> In this test created 4 project objects and map this objects to input source ODS file.
	 *
	 * 	<p> Сопоставление для какого проекта записывать числовые значения метрик в исх. файле происходит по ИД проекта.
	 * 	Т.е. ИД в объекте проекта должен быть найден в исх. файле.
	 * 	Соответственно ИД для всех проектов исх. файле должны быть установлены.
	 */
	@Test
	public void testSaveMetricsToSheet() {
//		String srcODS = System.getProperty("user.dir") + "/src/test/resources/projects-list_cp.ods";
		String srcODS = SRC_PROJECTS_LIST;
		final String sheetName = SHEET_FILE_MANAGERS;

		Project p3 = new Project();
		p3.setId("FM_J_03_PowerFileExplorer");
		Project.Metrics pr3Metrics = new Project().new Metrics(1, 2, 3, 4, 5, 6, 7);
		p3.setMetrics(pr3Metrics);

		Project p5 = new Project();
		p5.setId("FM_J_05_OI-File-Manager");
		Project.Metrics pr5Metrics = new Project().new Metrics(55, 12, 15, 17, 5, 9, 18);
		p5.setMetrics(pr5Metrics);

		Project p11 = new Project();
		p11.setId("FM_FCH_J_11_Android-Simple-File-Explorer-Library");
		Project.Metrics proj11Metrics = new Project().new Metrics(11, 190, 315, 19, 55, 77, 11);
		p11.setMetrics(proj11Metrics);

		Project p15 = new Project();
		p15.setId("FM_FCH_K_15_File-Picker-Library");
		Project.Metrics proj15Metrics = new Project().new Metrics(15, 1, 10, 15, 59, 100, 5);
		p15.setMetrics(proj15Metrics);

		ArrayList<Project> projects = new ArrayList<>();
		projects.add(p3);
		projects.add(p5);
		projects.add(p11);
		projects.add(p15);

//		Clear previous IDs in selected sheet in input source ODS file.
		try {
			SpreadSheet book = new SpreadSheet(new File(srcODS));
			Sheet sheet = book.getSheet(sheetName);
			sheet.appendColumns(ODSProcess.HEADER_COLUMNS.length);
			sheet.getRange("H2:N16").setValue(null);    // clear all values in sheet
			book.save(new File(srcODS));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		int savedProjects = new ODSProcess(srcODS, sheetName, Args.DEF_RANGE_PROC_ALL_ROWS).saveMetricsToSheet(projects);
		Assert.assertEquals(4, savedProjects);

		try {
			int loc = 0, nc = 1, nom = 2, dit = 3, wmc = 4, rfc = 5, cbo = 6;   // indexes of  metrics values in result array

			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);

			Object[][] m2dim = sheet.getRange("H4:N4").getValues();
			Object[] m = new Object[m2dim[0].length];
			System.arraycopy(m2dim[0], 0, m, 0, m2dim[0].length);
			String prm = m[loc] + "|" + m[nc] + "|" + m[nom] + "|" + m[dit] + "|" + m[wmc] + "|" + m[rfc] + "|" + m[cbo];
			Assert.assertEquals("1|2|3|4|5|6|7", prm);

			m2dim = sheet.getRange("H6:N6").getValues();
			m = new Object[m2dim[0].length];
			System.arraycopy(m2dim[0], 0, m, 0, m2dim[0].length);
			prm = m[loc] + "|" + m[nc] + "|" + m[nom] + "|" + m[dit] + "|" + m[wmc] + "|" + m[rfc] + "|" + m[cbo];
			Assert.assertEquals("55|12|15|17|5|9|18", prm);

			m2dim = sheet.getRange("H12:N12").getValues();
			m = new Object[m2dim[0].length];
			System.arraycopy(m2dim[0], 0, m, 0, m2dim[0].length);
			prm = m[loc] + "|" + m[nc] + "|" + m[nom] + "|" + m[dit] + "|" + m[wmc] + "|" + m[rfc] + "|" + m[cbo];
			Assert.assertEquals("11|190|315|19|55|77|11", prm);

			m2dim = sheet.getRange("H16:N16").getValues();
			m = new Object[m2dim[0].length];
			System.arraycopy(m2dim[0], 0, m, 0, m2dim[0].length);
			prm = m[loc] + "|" + m[nc] + "|" + m[nom] + "|" + m[dit] + "|" + m[wmc] + "|" + m[rfc] + "|" + m[cbo];
			Assert.assertEquals("15|1|10|15|59|100|5", prm);


		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
//	===========================================



//	===========================================
//      debug only
//	===========================================
	@Ignore
	@Test
	public void testReadSpreadsheetSingle() {
		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			Sheet fmSheet = book.getSheet("Sheet2");

			System.out.println("fmSheet.getMaxRows() = " + fmSheet.getMaxRows());
			System.out.println("fmSheet.getMaxColumns() = " + fmSheet.getMaxColumns());

			// i - rows counter
			// j - columns counter
			// first row - must be named title
			for (int i = 1; i < fmSheet.getMaxRows(); i++) {
				for (int j = 0; j < fmSheet.getMaxColumns(); j++) {
					Range range = fmSheet.getRange(i, j);
					System.out.print(range.getValue() + " | ");
				}
				System.out.println("\n---");
			}

			// set values to sheet
//			for (int i = 0; i < fmSheet.getMaxRows() - 1; i++) {
//				Range range = fmSheet.getRange(i, 0, 3);
//				System.out.println("range = " + range);
//			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Ignore
	@Test
	public void testSetValuesToSpreadSheetSingle() {
		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST));
			Sheet fmSheet = book.getSheet(SHEET_FILE_MANAGERS);

			for (int i = 0; i < fmSheet.getMaxRows(); i++) {
				if (i == 0) continue; // in first row - named title
				fmSheet.getRange(i, 1).setValue("abcd");
			}

			book.save(new File(SRC_PROJECTS_LIST));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Ignore
	@Test
	public void test0() {
		String srcODS = System.getProperty("user.dir") + "/src/test/resources/projects-list_cp.ods";
		try {
			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(SHEET_FILE_MANAGERS);

//			Get only one cell value, using "X and Y coordinates of cell" (variant 1)
			System.out.println("Variant 1:");
			String cellVal = sheet.getRange(2, 1).getValue().toString();
			System.out.println(cellVal);
//			Если указать диапазон getRange(0, 0), то получим "num".

//			Get only one cell value, using "A1 Notation format" (variant 2)
			System.out.println("Variant 2:");
			cellVal = sheet.getRange("B2").getValue().toString();
			System.out.println(cellVal);

//			Get range of cell values, using "X and Y coordinates of cell".
//			In our situation - range of row (num, name, id).
//			Parameters in method signature:
//			row: X - Coordinate of the starting cell
//			column:	Y - Coordinate of the starting cell
//			numRows: How many rows to take
//			numColumns:	How many columns to take
//			Пояснение параметров сигнатуры этого метода.
//			row. Т.к. отсчет строк и столбцов с 0, то координата строк где расположены данные будет == 1.
//			column. Далее 0 - с какого столбца выполнять получение данных.
//			numRows. Со скольки строк получать данные. В нашем примере нас интересует данные только по одному проекту, поэтому == 1.
//			numColumns. И последний параметр 3 - со скольки строк получать данные. Или по другому, со скольки ячеек () в выбранной строке (), начиная с ячейки ...  получать данные.
			System.out.println("Variant 3:");
			Object[][] cellValues = sheet.getRange(1, 0, 1, 3).getValues();
			System.out.println(cellValues[0][0]);
			System.out.println(cellValues[0][1]);
			System.out.println(cellValues[0][2]);

			System.out.println("Variant 4:");
			cellValues = sheet.getRange("A2:C2").getValues();
			System.out.println(cellValues[0][0]);
			System.out.println(cellValues[0][1]);
			System.out.println(cellValues[0][2]);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



//	===========================================


//	===========================================
//	    checkProjectsInSheet
//	===========================================
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testCheckProjectsInSheet_row2() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("not picked 'number' value.");

		new ODSProcess(SRC_ODS_PROJECTS_LIST_WRONG_FORMATS, "Sheet1", new int[]{2,5});
	}

	@Test
	public void testCheckProjectsInSheet_row3() {
		thrown.expect(WrongProjectCellFormatException.class);
		thrown.expectMessage("'number' has a non-numeric format");

		new ODSProcess(SRC_ODS_PROJECTS_LIST_WRONG_FORMATS, "Sheet1", new int[]{3,5});
	}

	@Test
	public void testCheckProjectsInSheet_row4() {
		thrown.expect(WrongProjectCellFormatException.class);
		thrown.expectMessage("value 'number' very big");

		new ODSProcess(SRC_ODS_PROJECTS_LIST_WRONG_FORMATS, "Sheet1", new int[]{4,5});
	}



	@Ignore
	@Test
	public void testCheckProjectsInSheet_rowa() {
		new ODSProcess(SRC_ODS_PROJECTS_LIST_WRONG_FORMATS, "Sheet2", new int[]{4,25});
	}

	@Ignore
	@Test
	public void testCheckProjectsInSheet_rowb() {
		new ODSProcess(SRC_PROJECTS_LIST, SHEET_FILE_MANAGERS, new int[]{1,1});
	}













//	===========================================

}