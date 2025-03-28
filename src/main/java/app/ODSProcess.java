package app;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import exception.WrongProjectCellFormatException;
import pojo.Project;
import util.Utils;
import util.UtilsODS;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ODSProcess {

	public static final int START_ROW_HEADER = 1;

	// FIXME: 15.03.25 Change javadoc
	/**
	 * Row from which projects data begins.
	 * First row (ODS Spreadsheet) and 0 row in SODS lib - title. And this row must be passed.
	 *
	 * IMPORTANT NOTE !
	 * Number of rows (and columns) in this SODS library started from 0.
	 * In ODS Spreadsheet rows (and columns) started from 1 and A respectively.
	 * +--------+-----------------+----------+
	 * |        | ODS Spreadsheet | SODS lib |
	 * +--------+-----------------+----------+
	 * | row    | 1               | 0        |
	 * | column | A               | 0        |
	 * +--------+-----------------+----------+
	 */
	public static final int START_ROW_PROJECTS = 2;

	public static final String NUM_COLUMN = "A";
	public static final String ID_COLUMN = "B";
	public static final String NAME_COLUMN = "C";
	public static final String TYPE_COLUMN = "D";
	public static final String SUBTYPE_COLUMN = "E";
	public static final String LANG_COLUMN = "F";
	public static final String URL_COLUMN = "G";

	public static final String[] HEADER_NAMES = {"LOC", "NC", "NoM", "DIT", "WMC", "RFC", "CBO"};
	public static final String[] HEADER_COLUMNS = {"H", "I", "J", "K", "L", "M", "N"};

	private final String srcODS;
	private final String sheetName;
	private final int[] range;

	public enum ExcludeChecks {NONE, ID, ID_NAME}

	private int lastProjectRow = -1;


	// TODO: 15.03.25 may be rn to ODSProc
	/**
	 *  Construct {@link ODSProcess} object of this class.
	 *  Main constructor.
	 *
	 *  <p> Когда создаем экземпляр этого класса и вызываем какой-то метод,
	 *  мы точно знаем с каким ODS файлом будем работать и на каком листе будет выполниться обработка проектов,
	 * 	а также и диапазон обрабатываемых проектов.
	 *
	 * 	<p> Все публичные методы в этом классе для выполнения тех или иных действий могли бы иметь одинаковую сигнатуру:
	 * 	String srcODS, String sheetName <br>
	 * 	или  <br>
	 * 	String srcODS, String sheetName, int[] range <br>
	 * 	Поэтому целесообразно сразу передавать эти параметры в сигнатуре конструктора данного класса.
	 *
	 * 	<p> Если не передан диапазон обрабатываемых строк, в конструкторе также выполняется автоматическое
	 * 	определение реальной последней строки, содержащей описания проектов. Делов том что метод sheet.getMaxRows()
	 * 	который должен возвращать номер последней строки (или по другому количество строк) содержащей
	 * 	какие-либо данные на листе, не всегда возвращает правильное значение.
	 * 	Автоматическое определение , основывается на том что для каждого проекта должны быть заполнены
	 * 	его характеристики в соответствующих ячейках (num, id, name, ...), кроме ячейки subtype.Если же
	 * 	хотя бы в одной из ячеек характеристика не указана - работа программы останавливается. Выдается одно
	 * 	из следующих исключений: NullPointerException - если определенная характеристика проекта не указана,
	 *  WrongProjectCellFormatException - если характеристика имеет неправильный формат и т.п..
	 *
	 *  Для обработки всех проектов на листе, в параметр range передаем new int[]{0, 0}.
	 *  Для обработки только определенных строк листа в котором содержатся проекты, в массиве указываем эти
	 *  номера строк, например new int[]{5, 15}.
	 *
	 * @param srcODS input source ODS file
	 * @param sheetName sheet name in selected input source ODS file
	 * @param range the range of rows in sheet in inout source ODS file containing projects.
	 * @see Args#DEF_RANGE_PROC_ALL_ROWS
	 */
	public ODSProcess(String srcODS, String sheetName, int[] range) {
		this.srcODS = srcODS;
		this.sheetName = sheetName;
		this.range = range;

		lastProjectRow = checkProjectsInSheet(srcODS, sheetName, range, ExcludeChecks.NONE);
	}

	/**
	 * Additional constructor most useful for unit testings.
	 *
	 * @param srcODS input source ODS file
	 * @param sheetName sheet name in selected input source ODS file
	 * @param range the range of rows in sheet in inout source ODS file containing projects.
	 * @param excludeChecks exclude checks in project characteristics in {@link #checkProjectsInSheet(String, String, int[], ExcludeChecks)} method.
	 * @see {@link ODSProcess} main constructor
	 */
	public ODSProcess(String srcODS, String sheetName, int[] range, ExcludeChecks excludeChecks) {
		this.srcODS = srcODS;
		this.sheetName = sheetName;
		this.range = range;

		lastProjectRow = checkProjectsInSheet(srcODS, sheetName, range, excludeChecks);
	}


	/**
	 * Проверка каждой строки в исходном ODS файле - является ли данная проверяемая строка, описанием программного проекта.
	 * Также данные метод возвращает "реальный" номер последней строки, содержащей описание проект.
	 *
	 * @param srcODS @see {@link ODSProcess#srcODS}
	 * @param sheetName @see {@link ODSProcess#sheetName}
	 * @param range @see {@link ODSProcess#range}
	 * @param excludeChecks
	 * @return "реальный" номер последней строки, содержащей описание проект
	 */
	public int checkProjectsInSheet(String srcODS, String sheetName, int[] range, ExcludeChecks excludeChecks) {
		int lastRow = -1; // last row count in selected sheet in input source ODS file contained data of projects.
		Object numObj, idObj, nameObj, typeObj, langObj, urlObj;

		boolean isCheckID = true, isCheckName = true;
		switch (excludeChecks) {
			case ID:
				isCheckID = false;
				break;

			case ID_NAME:
				isCheckID = false;
				isCheckName = false;
				break;
		}

		try {
			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);

			// This is where need to use the method sheet.getMaxRows() in order to go through
			// the maximum number of rows that this method returned.
			System.out.println("sheet.getMaxRows()=" + sheet.getMaxRows());

			// TODO: 19.03.25 move to single method
			int startRow = range[0];
			int endRow = range[1];
			if (startRow == 0 && endRow == 0) {
				startRow = START_ROW_PROJECTS;
				endRow = sheet.getMaxRows();
			}

//			Обратить внимание на сравнение row <= endRow !
			for (int row = startRow; row <= endRow; row++) {
				System.out.println("row = " + row);

//				Проверка 1
//				Иногда метод sheet.getMaxRows() возвращает корректные результаты последней строки содержащей
//				какие-либо данные, а иногда нет. Эта проверка отрабатывает как раз когда sheet.getMaxRows()
//				возвращает корректное значение.	В случае когда этот метод возвращает некорректное значение,
//				предназначена проверка 2. См. далее по коду.
//				if (row == sheet.getMaxRows()) return row;

				numObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, NUM_COLUMN)).getValue();
				idObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, ID_COLUMN)).getValue();
				nameObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, NAME_COLUMN)).getValue();
				typeObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, TYPE_COLUMN)).getValue();
				langObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, LANG_COLUMN)).getValue();
				urlObj= sheet.getRange(UtilsODS.convToA1NotationCell(row, URL_COLUMN)).getValue();

//				Проверка 2
//				Если в текущей проверяемой строке ни в одной ячейке не содержится любое значение
//				параметра программного проекта (все ячейки пустые) - считаем что описания проектов
//				закончены на текущем листе в ODS файле. И возвращаем предыдущий номер строки в которой содержалось описание проекта.
				if (numObj == null && idObj == null && nameObj == null && typeObj == null && langObj == null && urlObj == null) break;

				// .........................................
				//      Check number {@link NUM_COLUMN}
				// .........................................
				// Examples num: 01, 001, 155.
				// Since the int number contained in string presented in double type i.e.
				// this means additional two symbols: dot and zero.
				// For example: 1 -> '1.0', 15 -> '15.0', 170 -> '170.0'
				// This code not use method {@link Utils.convertDoubleStrToInt}.
				if (numObj == null) {
					throw new NullPointerException("For project in row " + row + " not picked 'number' value.");

				} else  {
					int numLen = numObj.toString().length();
					if (numLen > 5) {
						throw new WrongProjectCellFormatException("For project in row " + row + " value 'number' very big.");
					}
				}
				try {
					int num = (int) Double.parseDouble(numObj.toString());
//					System.out.println("num = " + num + " | " + "numObj = " + numObj);
				} catch (NumberFormatException e) {
					throw new WrongProjectCellFormatException("For project in row " + row + " value 'number' has a non-numeric format.");
				}
				// .........................................

				// .........................................
				//      Check ID {@link ID_COLUMN}
				// .........................................
				// Examples IDs: FM_J_03, FM_FCH_K_15, FM_FCH_K_15_File-Picker-Library  // TODO: 14.03.25 turn-off id check
				if (isCheckID) {
					if (idObj == null) {
						throw new NullPointerException("For project in row " + row + " not picked 'ID' value.");
					}
					int idLen = idObj.toString().length();
					if (idLen > 100) {
						throw new WrongProjectCellFormatException("For project in row " + row + " value 'ID' very long.");
					}
				}
				// .........................................

				// .........................................
				//      Check name {@link NAME_COLUMN}
				// .........................................
				// Examples names: 'File Manager', 'Dir', 'Android-Simple-File-Explorer-Library'
				if (isCheckName) {
					if (nameObj == null) {
						throw new NullPointerException("For project in row " + row + " not picked 'name' value.");
					}
					int nameLen = nameObj.toString().length();
					if (nameLen > 80) {
						throw new WrongProjectCellFormatException("For project in row " + row + " value 'name' very long.");
					}
				}
				// .........................................

				// TODO: 17.03.25 add checks: type, lang,url

				// Check is this row contain project data ?
//				if (projectRow.isProjectRow()) lastRow++;
				lastRow = row;
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("lastRow = " + lastRow);

		return lastRow;
	}

//	public int checkProjectsInSheet(String srcODS, String sheetName) {
//		return checkProjectsInSheet(srcODS, sheetName, new int[]{0, 0});
//	}

	/**
	 * <p> Obtain real last row contained data of projects in ODS Spreadsheet.
	 * The problem that the method sheet.getMaxRows() returned incorrect, larger number of rows contained
	 * real data of projects. For example last of project contained in the line 16. But the method sheet.getMaxRows()
	 * returned 21 row value. Which is not correct.
	 *
	 * <p>This method try to determine real LAST row containing data of projects in sheet in input source ODS file.
	 *
	 * <p> Important note !
	 * Number of rows in this SODS library started from 0. But in ODS Spreadsheet rows started from 1.
	 * See {@link #START_ROW_PROJECTS} .
	 *
	 * @return last row in input source ODS in selected sheet contained data of project.
	 */
	public int getRealLastProjectsRowFromSheet() {
		return lastProjectRow;
	}



	/**
	 *	<p> Get all (or selected from range) projects information in selected Sheet in input source ODS file.
	 * 	It means get next information (filled by user or generate semi-automatic) from input source ODS file:
	 * 	project number, ID, ... . All project information see in constants {@NUM_COLUMN}, ... </p>
	 *
	 * 	<p> Using range method parameter: </p>
	 * 	range[0] - start bound of row in selected sheet. <br>
	 * 	range[1] - end bound of row in selected sheet <br>
	 * 	Numbers of rows traditionally started from 1 as any spreadsheet. <br>
	 * 	Example in sheet: <br>
	 * 	...  <br>
	 * 	3 <- start bound row  <br>
	 * 	...  <br>
	 * 	15 <- end bound row  <br>
	 * 	...  <br>
	 * 	Put this range in to array method parameter new int[]{3, 15}  <br>
	 * 	It will be take rows from 3 to 15 .  <br>
	 *
	 * 	<p> Checks. Also in this method perform checks is each projects contain is none empty values.
	 * 	If any of checks is not passed - returned empty projects list. </p>
	 * 	Will be checks:  <br>
	 * 	number value {@link #NUM_COLUMN}  <br>
	 * 	ID value {@link #ID_COLUMN}  <br>
	 * 	type value {@link #TYPE_COLUMN}  <br>
	 * 	url value {@link #URL_COLUMN}  <br>
	 *
	 * @return obtained projects from sheet. If any of checks is not passed - returned empty projects list.
	 */
	public ArrayList<Project> getProjectsFromSheet() {
		ArrayList<Project> projects = new ArrayList<>();

		try {
			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);

			// i - rows counter
			// j - columns counter
			// first row - must be named title
			int startRow = range[0];
			int endRow = range[1];
			if (startRow == 0 && endRow == 0) {
				startRow = START_ROW_PROJECTS;
				endRow = lastProjectRow;
			}

//			endRow++;

			System.out.println("endRow = " + endRow);

			// Obtain columns
//			Обратить внимание на сравнение row <= endRow !
			for (int row = startRow; row <= endRow; row++) {   // i - row count
				// check projects in source ODS
//				boolean isPassChecks = true;    // assume that pass checks
				int num = Utils.convertDoubleStrToInt(sheet.getRange(UtilsODS.convToA1NotationCell(row, NUM_COLUMN)).getValue());
//
//				if (sheet.getRange(UtilsODS.convToA1NotationCell(row, NUM_COLUMN) ).getValue() == null) {
//					System.out.println("For project " + num + " not picked 'number' value.");
//					System.out.println("Exit.");
//					isPassChecks = false;
//				}
//
//				if (sheet.getRange( UtilsODS.convToA1NotationCell(row, ID_COLUMN) ).getValue() == null) {
//					System.out.println("For project " + num + " not picked 'ID' value.");
//					System.out.println("Exit.");
//					isPassChecks = false;
//				}
//
//				if (sheet.getRange( UtilsODS.convToA1NotationCell(row, TYPE_COLUMN) ).getValue() == null) {
//					System.out.println("For project " + num + " not picked 'type' value.");
//					System.out.println("Exit.");
//					isPassChecks = false;
//				}
//
//				if (sheet.getRange( UtilsODS.convToA1NotationCell(row, URL_COLUMN) ).getValue() == null) {
//					System.out.println("For project " + num + " not picked 'URL' value.");
//					System.out.println("Exit.");
//					isPassChecks = false;
//				}
//
//				if (!isPassChecks) return projects; // if any of checks is not passed returned empty projects list.
				// end checks

//				Obtain data for each project.
				String id = sheet.getRange(UtilsODS.convToA1NotationCell(row, ID_COLUMN)).getValue().toString();
				String name = sheet.getRange(UtilsODS.convToA1NotationCell(row, NAME_COLUMN)).getValue().toString();
				String type = sheet.getRange(UtilsODS.convToA1NotationCell(row, TYPE_COLUMN)).getValue().toString();

				String subType = "";
				Object subTypeObj = sheet.getRange(UtilsODS.convToA1NotationCell(row, SUBTYPE_COLUMN)).getValue();
				if (subTypeObj != null) subType = subTypeObj.toString();

				String lang = sheet.getRange(UtilsODS.convToA1NotationCell(row, LANG_COLUMN)).getValue().toString();
				String url = sheet.getRange(UtilsODS.convToA1NotationCell(row, URL_COLUMN)).getValue().toString();

//				System.out.println(num + " | " + id + " | "	+ name + " | "	+ type + " | " + subType + " | " + lang + " | "	+ url);
				projects.add( new Project(row, num, id, name, type, subType, lang, url) );
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return projects;
	}

	// TODO: 19.03.25 may be rn to generateNamesFromSheet (from)
	// TODO: 19.03.25 must be added range functionality
	/**
	 * <p>Generate Name for all projects in selected sheet.
	 * This method NOT save generated Names to input source ODS file, but only read this file and returned Names !
	 * If cell will be already contained name it will be rewritten !
	 * Names taken from URL.
	 *
	 * <p> Examples: <br>
	 * "https://github.com/SimpleMobileTools/Simple-File-Manager" -> Simple-File-Manager  <br>
	 *  "https://github.com/PowerExplorer/PowerFileExplorer" -> PowerFileExplorer <br>
	 *  "https://github.com/aklinker1/fixate5/" -> fixate5 <br>
	 *
	 * @return HashMap collection contained: key - number of row in input source ODS file, value - generated Name of the project.
	 */
	public Hashtable<Integer, String> generateNamesInSheet() {
		Hashtable<Integer, String> names = new Hashtable<>();

		try {
			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);

			for (int i = START_ROW_PROJECTS; i <= lastProjectRow; i++) {
				String url = sheet.getRange(UtilsODS.convToA1NotationCell(i, URL_COLUMN)).getValue().toString();
				String name = Utils.getLastNameInPath(url);
				name = name.replaceAll(" ", "-");

				names.put(i, name);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return names;
	}

	// TODO: 19.03.25 may be rn to generateIDsFromSheet (from)
	// TODO: 19.03.25 must be added range functionality
	/**
	 * <p>Generate IDs for all projects in selected sheet.
	 * This method NOT save generated IDs to input source ODS file, but only read this file and returned IDs !
	 *
	 * <p> Format ID: type_(subtype)_lang_num_name
	 * <p> Examples: FM_J_03_PowerFileExplorer, FM_K_09_fixate5, FM_FCH_K_15_File-Picker-Library
	 *
	 * @return HashMap collection contained: key - number of row in input source ODS file, value - generated ID of the project.
	 */
	public Hashtable<Integer, String> generateIDsInSheet(){
		// key - number of row, generated ID of project contains in this row.
		// value - generated ID of the project.
		Hashtable<Integer, String> ids = new Hashtable<>();

		try {
			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);

			for (int i = START_ROW_PROJECTS; i <= lastProjectRow; i++) {
				String type = sheet.getRange(UtilsODS.convToA1NotationCell(i, TYPE_COLUMN)).getValue().toString();

				String subType = "";
				Object subTypeObj = sheet.getRange(UtilsODS.convToA1NotationCell(i, SUBTYPE_COLUMN)).getValue();
				if (subTypeObj != null) subType = "_" + subTypeObj;

				String lang = sheet.getRange(UtilsODS.convToA1NotationCell(i, LANG_COLUMN)).getValue().toString();

				int numInt = Utils.convertDoubleStrToInt(sheet.getRange(UtilsODS.convToA1NotationCell(i, NUM_COLUMN)).getValue().toString());
				String num = String.format("%02d", numInt);

				String name = sheet.getRange(UtilsODS.convToA1NotationCell(i, NAME_COLUMN)).getValue().toString();
				name = name.replaceAll(" ", "-");

				// Generate resulting ID of the project
				// TODO: 17.03.25 may change pattern for projects ID to as example: 15_FM_FCH_K_File-Picker-Library  
				String id = type + subType + "_" + lang + "_" + num + "_" + name;
				ids.put(i, id);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return ids;
	}

	/**
	 * <p> Save given Names for all projects in selected sheet.
	 *
	 * @param names HashMap collection contained: key - number of row in input source ODS file, value - generated Name of the project.
	 *
	 * @return numerical value processed projects
	 */
	public int saveNamesToSheet(Hashtable<Integer, String> names) {
		int count = 0;  // saved projects count
		try {
			SpreadSheet book = new SpreadSheet(new File(srcODS));
			Sheet sheet = book.getSheet(sheetName);

			// By default, Hashtable not sorting in alphabet order.
			// Sorting can be done the list of keys extracted from the HashTable and obtaining values in that order.
			ArrayList<Integer> tmpList = Collections.list(names.keys());
			Collections.sort(tmpList);

			for (Integer row : tmpList) {
				String name = names.get(row);
				System.out.println("name = " + name);
				sheet.getRange(UtilsODS.convToA1NotationCell(row, NAME_COLUMN)).setValue(name);
				count++;
			}
			book.save(new File(srcODS));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return count;
	}

	/**
	 * <p> Save given IDs for all projects in selected sheet.
	 *
	 * @param ids HashMap collection contained: key - number of row in input source ODS file, value - generated ID of the project.
	 *
	 * @return numerical value processed projects
	 */
	public int saveIDsToSheet(Hashtable<Integer, String> ids) {
		int count = 0;  // saved projects count
		try {
			SpreadSheet book = new SpreadSheet(new File(srcODS));
			Sheet sheet = book.getSheet(sheetName);

			// By default, Hashtable not sorting in alphabet order.
			// Sorting can be done the list of keys extracted from the HashTable and obtaining values in that order.
			ArrayList<Integer> tmpList = Collections.list(ids.keys());
			Collections.sort(tmpList);

			for (Integer row : tmpList) {
				String id = ids.get(row);
				System.out.println("id = " + id);
				sheet.getRange(UtilsODS.convToA1NotationCell(row, ID_COLUMN)).setValue(id);
				count++;
			}
			book.save(new File(srcODS));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return count;
	}

	/**
	 * <p> Save to input source ODS file all numerical values of metrics for each given project.
	 * Numerical values of metrics will be written ONLY if project in given sheet existing.
	 * Otherwise, those values not be written.
	 * 
	 * <p> Amount of projects for which values have been written can be controlled using returned count.
	 * This count may be compared with given projects array.
	 *
	 * @param projects
	 * @return numerical value saved projects
	 */
	public int saveMetricsToSheet(ArrayList<Project> projects) {
		int savedProjectsCount = 0;

		try {
			SpreadSheet book = new SpreadSheet(new File(srcODS));
			Sheet sheet = book.getSheet(sheetName);

			// TODO: 15.03.25 Определить, должен ли обязательно быть указан ИД проекта в соотв. объекте ?
			// FIXME: 12.03.25 may be refactored this loop
//		    Find project given in ArrList in sheet in source ODS file by their ID.
//			ИД проекта может быть и не указан в объекте проекта, поэтому находим его непосредственно в выбранном листе в ODS документе.
			for (int odsRow = START_ROW_PROJECTS; odsRow <= lastProjectRow; odsRow++) {
//				String odsID = sheet.getRange(odsRow, ID_COLUMN).getValue().toString();
				String odsID;
				Object odsIDObj = sheet.getRange(UtilsODS.convToA1NotationCell(odsRow, ID_COLUMN)).getValue();
				// check in situation if cell ID in ODS in sheet is empty (method getValue returned null)
				if (odsIDObj != null) odsID = odsIDObj.toString(); else continue;

				for (Project project : projects) {
					String prID = project.getId();

					if (odsID.equals(prID)) {
						project.setRow(odsRow);
					}
				}
			}

//			Without this throw exception: Range goes out of bounds: (end_of_range: 14, maxcolumns in sheet: 7
			sheet.appendColumns(HEADER_COLUMNS.length);

//			Generate range where inserted metrics header (usually in row == 1)
//			left and right bound of range for inserted metrics header.
			// TODO: 12.03.25 refactoring: will use convToA1NotationRange method
			String lb = HEADER_COLUMNS[0] + START_ROW_HEADER;    // Example: H1
			String rb = HEADER_COLUMNS[HEADER_COLUMNS.length - 1] + START_ROW_HEADER; // Example: N1
			String range = lb + ":" + rb; // Example: H1:N1
			System.out.println("range header = " + range);
			sheet.getRange(range).setValues((Object[]) HEADER_NAMES);

			for (Project pr : projects) {
//				Generate range where inserted numerical values of metrics.
				lb = HEADER_COLUMNS[0] + pr.getRow();
				rb = HEADER_COLUMNS[HEADER_COLUMNS.length - 1] + pr.getRow();
				range = lb + ":" + rb;  // Example: H5:N5
				System.out.println("range metrics values = " + range);

				Object[] metricValues = {
						String.valueOf(pr.getMetrics().getLOC()),
						String.valueOf(pr.getMetrics().getNC()),
						String.valueOf(pr.getMetrics().getNoM()),
						String.valueOf(pr.getMetrics().getDIT()),
						String.valueOf(pr.getMetrics().getWMC()),
						String.valueOf(pr.getMetrics().getRFC()),
						String.valueOf(pr.getMetrics().getCBO())
				};

				sheet.getRange(range).setValues(metricValues);
				savedProjectsCount++;
			}

			book.save(new File(srcODS));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return savedProjectsCount;
	}

//	/**
//	 * <p> Obtain real last row contained data of projects in ODS Spreadsheet.
//	 * The problem that the method sheet.getMaxRows() returned incorrect, larger number of rows contained
//	 * real data of projects. For example last of project contained in the line 16. But the method sheet.getMaxRows()
//	 * returned 21 row value. Which is not correct.
//	 *
//	 * <p>This method try to determine real LAST row containing data of projects in sheet in input source ODS file.
//	 *
//	 * <p> Important note !
//	 * Number of rows in this SODS library started from 0. But in ODS Spreadsheet rows started from 1.
//	 * See {@link #START_ROW} .
//	 *
//	 * @param srcODS input source ODS file
//	 * @param sheetName sheet name in selected input source ODS file
//	 * @return last row in source ODS in selected sheet contained data of project.
//	 */
//	public int getRealLastProjectsRowFromSheet(String srcODS, String sheetName) {
//		ProjectRow projectRow;
//		int lastRow = START_ROW; // last row count in selected sheet in input source ODS file contained projects.
//
//		try {
//			Sheet sheet = new SpreadSheet(new File(srcODS)).getSheet(sheetName);
//
//			// This is where need to use the method sheet.getMaxRows() in order to go through
//			// the maximum number of rows that this method returned.
//			System.out.println("sheet.getMaxRows()=" + sheet.getMaxRows());
//			for (int row = START_ROW; row < sheet.getMaxRows(); row++) {
//				projectRow = new ProjectRow(
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, NUM_COLUMN)).getValue(),
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, ID_COLUMN)).getValue(),
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, NAME_COLUMN)).getValue(),
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, TYPE_COLUMN)).getValue(),
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, LANG_COLUMN)).getValue(),
//						sheet.getRange(UtilsODS.convToA1NotationCell(row, URL_COLUMN)).getValue()
//				);
//
//				// Check is this row contain project data ?
//				if (projectRow.isProjectRow()) lastRow++;
//			}
////			lastRow ++; // TODO: 12.03.25 искусственно увеличиваем последнюю строку чтобы не изменять условие на row <= endRow в цикле
//
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		return lastRow;
//	}

//	private static class ProjectRow {
//		private Object numObj, idObj, nameObj, typeObj, langObj, urlObj;
//		private boolean isNum = false, isID = false, isName = false, isType = false, isLang = false, isURL = false;
//		// TODO: 04.02.25 Since available option to generate and save IDs from other project data, may not check ids cell ?
//		private boolean isProcessID = false;
//
////		public enum CheckLevels {
////			ALL, NUM_ID_TYPE_URL
////		}
//
//		/**
//		 * Return codes after checks is this row contain project data ?
//		 */
////		public enum RetCode {
////			SUCCESS("SUCCESS"),
////			ERR_NUM("Error in 'number' cell"),
////			ERR_ID("Error in 'ID' cell"),
////			ERR_NAME("Error in 'name' cell"),
////			ERR_TYPE("Error in 'type' cell"),
////			ERR_LANG("Error in 'lang' cell"),
////			ERR_URL("Error in 'URL' cell");
////
////			private final String text;
////
////			RetCode(final String text) {
////				this.text = text;
////			}
////
////			@Override
////			public String toString() {
////				return text;
////			}
////		}
//
//		public ProjectRow(Object numObj, Object idObj, Object nameObj, Object typeObj, Object langObj, Object urlObj) {
//			this.numObj = numObj;
//			this.idObj = idObj;
//			this.nameObj = nameObj;
//			this.typeObj = typeObj;
//			this.langObj = langObj;
//			this.urlObj = urlObj;
//		}
//
//		public boolean isProjectRow() {
//			// Examples: 01, 001, 155.
//			// Since the int number contained in string presented in double type i.e.
//			// this means additional two symbols: dot and zero.
//			// For example: 1 -> '1.0', 15 -> '15.0', 170 -> '170.0'
//			// This code not use method {@link Utils.convertDoubleStrToInt}.
//			if (numObj != null) {
//				int numLen = numObj.toString().length();
//				if (numLen > 0 && numLen < 5) isNum = true;
//			}
//
//			// Examples: FM_J_03, FM_FCH_K_15, FM_FCH_K_15_File-Picker-Library
//			if (idObj != null) {
//				int idLen = idObj.toString().length();
//				if (idLen > 0 && idLen < 100) isID = true;
//			}
//			if (!isProcessID) isID = true;
//
//			// Examples: 'File Manager', 'Dir', 'Android-Simple-File-Explorer-Library'
//			if (nameObj != null) {
//				int nameLen = nameObj.toString().length();
//				if (nameLen > 0 && nameLen < 80) isName = true;
//			}
//
//			// Examples: FM
//			if (typeObj != null) {
//				int typeLen = typeObj.toString().length();
//				if (typeLen > 0 && typeLen < 10) isType = true;
//			}
//
//			// subType not check because this parameter may be omitted.
//
//			// Examples: J, K
//			if (langObj != null) {
//				int langLen = langObj.toString().length();
//				if (langLen > 0 && langLen < 5) isLang = true;
//			}
//
//			// url length can not be less 15 symbols.
//			// For example string 'https://github.com' contain 18 symbols.
//			// Examples: https://github.com/Atwa/filepicker
//			if (urlObj != null) {
//				int urlLen = urlObj.toString().length();
//				if (urlLen > 15) isURL = true;
//			}
//
//			return (isNum && isID && isName && isType && isLang && isURL);
//		}
//	}



}
