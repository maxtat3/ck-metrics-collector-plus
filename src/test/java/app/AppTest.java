package app;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ExpectedException;
import web.GitHubDownloaderTest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

public class AppTest {

	/**
	 * Для того чтобы увидеть полный вывод в терминал при запуске тестов,
	 * нужно отключить перехват System.out.println.
	 * Для этого нужно просто закомментировать строку App.setSysOutDelegate(new TestSysOut())
	 * в методе injectLastSysOutDelegate, который вызывается перед началом тестов.
	 * Для этого и предназначен данный флаг.
	 */
//	private static final boolean isAllowSysOutDelegate = true;
	private static final boolean isAllowSysOutDelegate = false;

	private static String lastSysOutMsg;

	private static class TestSysOut implements SysOutDelegate {
		@Override
		public void println(String msg) {
			lastSysOutMsg = msg;
		}
	}

	@BeforeClass
	public static void injectLastSysOutDelegate() {
		if (isAllowSysOutDelegate) App.setSysOutDelegate(new TestSysOut());
	}


//	https://github.com/junit-team/junit4/wiki/exception-testing
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// Test ODS file contain 15 projects: rows 2...16
	private static final String SRC_PROJECTS_LIST = "src/test/resources/projects-list.ods";

	private static final String SRC_PROJECTS_LIST_3PROJECTS = "src/test/resources/projects-list_3pr.ods";

	private static final String DEST_DIR_NOT_EMPTY = "src/test/resources/dir-not-empty";
	private static final String DEST_DIR_EMPTY = "src/test/resources/dir-empty";
	private static final String DEST_PATH_IS_FILE = "src/test/resources/dir-not-empty/file.txt";

	/**
	 * Path to input source ODS file contained description of already downloaded projects.
	 * @see #SRC_ROOT_DIR_PROJECTS
	 */
	private static final String SRC_PROJECTS_LIST_7PROJECTS = "srcProjectsBundle/projects-list_7pr.ods";

	/**
	 * Path to directory with already downloaded projects for obtain numerical values of metrics
	 * @see #SRC_PROJECTS_LIST_7PROJECTS
	 */
	private static final String SRC_ROOT_DIR_PROJECTS = "srcProjectsBundle/";

// +++
//	===========================================
//      Help
//	===========================================
	@Test
	public void testHelp0() {
		String[] args = {
		};
		App.main(args);
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Not passed arguments."));
	}

	@Test
	public void testHelp1() {
		String[] args = {
			"-?"
		};
		App.main(args);
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Examples:"));
	}

	@Test
	public void testHelp2() {
		String[] args = {
			"--help"
		};
		App.main(args);
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Examples:"));
	}
//	===========================================

// ...
//	===========================================
//		Not enough CLI arguments
//	===========================================
	@Test
	public void testNotEnoughArgs_inODSOnly_err() {
		String[] args = {
			"--input-ods", SRC_PROJECTS_LIST_7PROJECTS,
		};
		App.main(args);
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Mode must be selected."));
	}

	@Test
	public void testNotEnoughArgs_inODSAndMode_err() {
		String[] args = {
			"--input-ods", SRC_PROJECTS_LIST_7PROJECTS,
			"--mode", "METRICS",
		};
		App.main(args);
		// TODO: 01.03.25 не удается протестировать, потому что sysoutdelegate вызывается в разных методах.
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Input projects dir must be passed."));
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Sheet name must be passed"));
	}

	@Test
	public void testNotEnoughArgs_inODSAndInDirAndMode_err() {
		String[] args = {
			"--input-ods", SRC_PROJECTS_LIST_7PROJECTS,
			"--input-dir", SRC_ROOT_DIR_PROJECTS,
			"--mode", "METRICS",
		};
		App.main(args);
		// TODO: 01.03.25 не удается протестировать, потому что sysoutdelegate вызывается в разных методах.
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Sheet name must be passed"));
	}

//	===========================================


//	===========================================
//      Sign mode
//	===========================================
//  +++
	@Test
	public void testSign_correct() {
//		Clear previous IDs in selected sheet after run test.
		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST_3PROJECTS));
			Sheet sheet = book.getSheet("Sheet1");
			sheet.getRange("B2:B4").setValue(null);
			sheet.getRange("C2:C4").setValue(null);
			book.save(new File(SRC_PROJECTS_LIST_3PROJECTS));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--sheet-name", "Sheet1",
				"--mode", "SIGN",
		};
		App.main(args);

		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("3 'names' and 'IDs' saved in input source ODS file."));
	}

	//  +++
	@Test
	public void testSign_notExistSheet_wrong() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("Cannot invoke \"com.github.miachm.sods.Sheet.getMaxRows()\" because \"sheet\" is null");

		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--sheet-name", "Sheet_a",
				"--mode", "SIGN",
		};
		App.main(args);
	}

	// TODO: 20.03.25 Возможно сделать проверку что лист не содержит данных о проектах.
//	Если передать пустой лист, то будет следующее сообщение:
//	0 'names' and 'IDs' saved in input source ODS file.
//Finish.
	@Ignore
	@Test
	public void testSign_passBlankSheet() {
		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--sheet-name", "BlankSheet",
				"--mode", "SIGN",
		};
		App.main(args);
	}
//	===========================================


//	===========================================
//      Download mode
//	===========================================
//	+++
	@Test
	public void testDownloadProjects_destDirEmpty_correct() {
		String DEST_TMP_DIR_EMPTY;
		try {
			DEST_TMP_DIR_EMPTY = Files.createTempDirectory("dwlGitRepos").toFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--mode", "DOWNLOAD",
				"--sheet-name", "Sheet1",
				"--destination", DEST_TMP_DIR_EMPTY
		};
		App.main(args);

		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Finish"));

		if (lastSysOutMsg.contains("Finish")) {
			final int[] dirCount = {0};
			new File(DEST_TMP_DIR_EMPTY).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (dir.isDirectory()) dirCount[0]++;
					return true;
				}
			});

			Assert.assertEquals(3, dirCount[0]);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			GitHubDownloaderTest.removeDestDir();
		}
	}

//	+++
	@Test
	public void testDownloadProjects_destDirNotEmpty_wrong() {
		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--mode", "DOWNLOAD",
				"--sheet-name", "Sheet1",
				"--destination", DEST_DIR_NOT_EMPTY
		};
		App.main(args);

		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Destination directory must be empty"));
	}

//	+++
	@Test
	public void testDownloadProjects_destDirIsFile_wrong() {
		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_3PROJECTS,
				"--mode", "DOWNLOAD",
				"--sheet-name", "Sheet1",
				"--destination", DEST_PATH_IS_FILE
		};
		App.main(args);

		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Destination path is not directory"));
	}

	@Test
	public void testDownloadProjects_ignore5Projects_correct() {
		String DEST_TMP_DIR_EMPTY;
		try {
			DEST_TMP_DIR_EMPTY = Files.createTempDirectory("dwlGitRepos").toFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST,
				"--mode", "DOWNLOAD",
				"--sheet-name", "FileManagers",
				"--ignore-rows", "5,6,9,14,15",
				"--destination", DEST_TMP_DIR_EMPTY
		};
		App.main(args);

		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Finish"));

		if (lastSysOutMsg.contains("Finish")) {
			final int[] dirCount = {0};
			new File(DEST_TMP_DIR_EMPTY).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (dir.isDirectory()) dirCount[0]++;
					return true;
				}
			});

			Assert.assertEquals(3, dirCount[0]);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			GitHubDownloaderTest.removeDestDir();
		}
	}
//	===========================================


//	===========================================
//      Metrics mode
//	===========================================
	@Test
	public void testMetricsMode_inODSAndInDirAndSheetAndMode_succ() {
		String[] args = {
				"--input-ods", SRC_PROJECTS_LIST_7PROJECTS,
				"--input-dir", SRC_ROOT_DIR_PROJECTS,
				"--sheet-name","Sheet1",
				"--mode", "METRICS",
		};
		App.main(args);
//		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Select retrieve METRICS from already downloaded projects"));
		Assert.assertThat(lastSysOutMsg, CoreMatchers.containsString("Processed 7 projects"));

		metricsModeCheckMetricsValuesInSrcODS();
	}

	private void metricsModeCheckMetricsValuesInSrcODS() {
		String pr1m, pr2m, pr3m, pr4m, pr5m, pr6m, pr7m;
		try {
			SpreadSheet book = new SpreadSheet(new File(SRC_PROJECTS_LIST_7PROJECTS));
			Sheet sheet = book.getSheet("Sheet1");
			Object[][] m = sheet.getRange("H2:N8").getValues();

//			System.out.println("sheet.getMaxRows() = " + sheet.getMaxRows());
//			System.out.println("sheet.getMaxColumns() = " + sheet.getMaxColumns());

			int pr1 = 0, pr2 = 1, pr3 = 2, pr4 = 3, pr5 = 4, pr6 = 5, pr7 = 6; // indexes of projects in result array
			int loc = 0, nc = 1, nom = 2, dit = 3, wmc = 4, rfc = 5, cbo = 6;   // indexes of  metrics values in result array
			pr1m = m[pr1][loc] + "|" + m[pr1][nc] + "|" + m[pr1][nom] + "|" + m[pr1][dit] + "|" + m[pr1][wmc] + "|" + m[pr1][rfc] + "|" + m[pr1][cbo];
			pr2m = m[pr2][loc] + "|" + m[pr2][nc] + "|" + m[pr2][nom] + "|" + m[pr2][dit] + "|" + m[pr2][wmc] + "|" + m[pr2][rfc] + "|" + m[pr2][cbo];
			pr3m = m[pr3][loc] + "|" + m[pr3][nc] + "|" + m[pr3][nom] + "|" + m[pr3][dit] + "|" + m[pr3][wmc] + "|" + m[pr3][rfc] + "|" + m[pr3][cbo];
			pr4m = m[pr4][loc] + "|" + m[pr4][nc] + "|" + m[pr4][nom] + "|" + m[pr4][dit] + "|" + m[pr4][wmc] + "|" + m[pr4][rfc] + "|" + m[pr4][cbo];
			pr5m = m[pr5][loc] + "|" + m[pr5][nc] + "|" + m[pr5][nom] + "|" + m[pr5][dit] + "|" + m[pr5][wmc] + "|" + m[pr5][rfc] + "|" + m[pr5][cbo];
			pr6m = m[pr6][loc] + "|" + m[pr6][nc] + "|" + m[pr6][nom] + "|" + m[pr6][dit] + "|" + m[pr6][wmc] + "|" + m[pr6][rfc] + "|" + m[pr6][cbo];
			pr7m = m[pr7][loc] + "|" + m[pr7][nc] + "|" + m[pr7][nom] + "|" + m[pr7][dit] + "|" + m[pr7][wmc] + "|" + m[pr7][rfc] + "|" + m[pr7][cbo];

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Assert.assertEquals("2515|13|118|55|398|568|214", pr1m);
		Assert.assertEquals("1080|21|123|47|210|304|191", pr2m);
		Assert.assertEquals("35999|216|3177|827|7319|6591|2877", pr3m);
		Assert.assertEquals("984|16|122|26|171|292|117", pr4m);
		Assert.assertEquals("436|28|99|42|109|117|119", pr5m);
		Assert.assertEquals("2308|9|268|23|626|352|53", pr6m);
		Assert.assertEquals("7075|76|778|128|1265|2521|836", pr7m);
	}

//	===========================================


//	===========================================
//      Debug only
//	===========================================
// Related:
//	ProcessorTest.testWriteMetricsToDestCSVFile
//	ProcessorTest.testCalcMetrics_pr1 ... pr7
	@Test
	public void testGetMetrics_1() {
		String[] args = {
				"--input-dir", SRC_ROOT_DIR_PROJECTS,
				"--mode", "METRICS",
				"--destination", SRC_ROOT_DIR_PROJECTS
		};
		App.main(args);

	}

//	...........................................
//      test related and not related projects (ODS <-> projDir)
//	...........................................
	// test 1
	// projects-all-related.ods
	// dir1: 3 projects
	// 3 pr in ODS, same 3 pr in FS

	// test 2
	// projects-ods-present-projdir-not.ods
	// dir1: ---//--- same as test 1
	// 5 pr in ODS, but only 3 pr in FS

	// test 3
	// projects-ods-not-projdir-present.ods
	// dir2: 5 projects
	// 3 pr in ODS, 5 pr in FS (2 of them not present in ODS)

	private static final String INPUT_FILE_PROJ_RELATED_TEST_1 = "src/test/resources/projects-related/projects-all-related.ods";
	private static final String INPUT_FILE_PROJ_RELATED_TEST_2 = "src/test/resources/projects-related/projects-ods-not-projdir-present.ods";
	private static final String INPUT_FILE_PROJ_RELATED_TEST_3 = "src/test/resources/projects-related/projects-ods-present-projdir-not.ods";

	private static final String INPUT_ROOT_DIR_PROJ_RELATED_TESTS_1_2 = "src/test/resources/projects-related/projects-test1and2/";
	private static final String INPUT_ROOT_DIR_PROJ_RELATED_TEST3 = "src/test/resources/projects-related/projects-test3/";

	@Test
	public void testGetMetrics_projectsAllRelated() {
		String[] args = {
				"--input-ods", INPUT_FILE_PROJ_RELATED_TEST_1,
				"--input-dir", INPUT_ROOT_DIR_PROJ_RELATED_TESTS_1_2,
				"--mode", "METRICS",
				"--sheet-name", "Sheet1"
		};
		App.main(args);
	}


	@Test
	public void testGetMetrics_partlyRelatedODSPresentProjDirNot() {
		String[] args = {
				"--input-ods", INPUT_FILE_PROJ_RELATED_TEST_2,
				"--input-dir", INPUT_ROOT_DIR_PROJ_RELATED_TESTS_1_2,
				"--mode", "METRICS"
		};
		App.main(args);
	}

	@Test
	public void testGetMetrics_partlyAllRelatedODSNotProjDirPresent() {
		String[] args = {
				"--input-ods", INPUT_FILE_PROJ_RELATED_TEST_3,
				"--input-dir", INPUT_ROOT_DIR_PROJ_RELATED_TEST3,
				"--mode", "METRICS"
		};
		App.main(args);
	}

//  ...........................................
//	===========================================
}