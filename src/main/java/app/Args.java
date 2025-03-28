package app;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Args {

	public static final String MODE_SIGN = "SIGN";
	public static final String MODE_DOWNLOAD = "DOWNLOAD";
	public static final String MODE_METRICS = "METRICS";


	@Parameter(names = {"-?", "--help"}, help = true, description = "Show help, examples and how to use this app.", order = 0)
	private boolean help;

	@Parameter(names = {"-i1", "--input-ods"}, description = "<FILE> absolute path to input (source) ODS file.", order = 1)
	private String inputODS;

	@Parameter(names = {"-i2", "--input-dir"}, description = "<DIR> absolute path to directory with already downloaded projects (for "+MODE_METRICS+" mode only).", order = 2)
	private String inputDir;

	@Parameter(names = {"-s", "--sheet-name"}, description = "<NAME>", order = 3)
	private String sheetName;

	@Parameter(names = {"-r", "--range"}, description = "Selected range of rows in format: <START:END>. Rows started from ...", order = 4)
	private String range;

	@Parameter(names = {"-g", "--ignore-rows"}, description = "Ignoring rows contained projects whet it downloading." +
			"This rows must be separated by commas, without spaces. Example: 1,5,16,24,25 This option using for "+MODE_DOWNLOAD+" mode only).")
	private String ignoreRows;

	@Parameter(names = {"-m", "--mode"}, description = "variants: {" + MODE_SIGN + " | " + MODE_DOWNLOAD + " | " + MODE_METRICS + "}\n" +
			MODE_SIGN + " - auto sign cells 'name' and 'ID' for project. 'Name' take from URL. 'ID' take from multiply project characteristics.\n" +
			MODE_DOWNLOAD + " - download projects taken from GitHub hosting to destination directory.\n" +
			MODE_METRICS + " - obtain metrics from already downloaded projects and save to input ODS file.", order = 5)
	private String mode;

	@Parameter(names = {"-d", "--destination"}, description = "<DIR> absolute path to destination directory for will be downloading projects (for "+MODE_DOWNLOAD+" mode only).", order = 6)
	private String destination;


	public boolean isHelp() {
		return help;
	}

	public String getInputODS() {
		return inputODS;
	}

	public String getInputDir() {
		return inputDir;
	}

	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Processing all rows in input source ODS in selected sheet.
	 */
	public static final int[] DEF_RANGE_PROC_ALL_ROWS = new int[]{0, 0};

	/**
	 * Range numbers of rows will be processed.
	 *
	 * @return integer array presented START and END rows of range.
	 * Projects contained in this rows will be processed.
	 * Array elements: 0 - START row , 1 - END row
	 */
	public int[] getRange() {
		if (range == null) {
			return DEF_RANGE_PROC_ALL_ROWS;
		}

		String[] rangeStrArr = range.split(":");
		int start, end;
		try {
			start = Integer.parseInt(rangeStrArr[0]);
			end = Integer.parseInt(rangeStrArr[1]);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
		return new int[] {start, end};
	}

//	public static final int[] DEF_VAL_NOT_IGNORE_ANY_ROWS = new int[]{0};

	public List<Integer> getIgnoreRows() {
		if (ignoreRows == null) {
			return new ArrayList<>();
		}
		String[] rowsStrArr = ignoreRows.split(",");
		int[] rowsIntArr = Arrays.stream(rowsStrArr).mapToInt(Integer::parseInt).toArray();
		return Arrays.stream(rowsIntArr).boxed().collect(Collectors.toList());
	}

	public String getMode() {
		return mode;
	}

	public String getDestination() {
		return destination;
	}
}
