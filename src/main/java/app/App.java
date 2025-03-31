package app;

import com.beust.jcommander.JCommander;
import org.eclipse.jgit.api.errors.GitAPIException;
import pojo.Project;
import util.Utils;
import web.GitHubDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class App {


	public static String getAppVersion() {
		AppProps.load();
		return AppProps.get("MAJOR", "x") + "." + AppProps.get("MINOR", "x");
	}

	public static String getAppNameAndVer() {
//		return AppProps.APP_NAME + " " + getAppVersion();   // TODO: 28.02.25 not work at now ...
		return "app 1.0";
	}


	private static SysOutDelegate sod = new AppSysOut();

	private static class AppSysOut implements SysOutDelegate {
		@Override
		public void println(String val) {
			System.out.println(val);
		}
	}

//	private static class AppSysOut implements SysOutDelegate {
//		private String message = "";
//
//		@Override
//		public void add(String msg){
//			message += msg;
//	        message += "\n";
//		}
//
//		@Override
//		public void println(String msg) {
//			System.out.println(msg);
//		}
//	}

	protected static void setSysOutDelegate(SysOutDelegate sysOutDelegate) {
		App.sod = sysOutDelegate;
	}


	private static String[] argv;

	public App(String[] argv) {
		this.argv = argv;
	}


	public static void main(String[] args) {
		App app = new App(args);

		Args cliArgs = new Args();
		JCommander.newBuilder()
				.addObject(cliArgs)
				.build()
				.parse(app.argv);

		int len = args.length;

		if (len == 0) {
			String msg = "";
			msg += getAppNameAndVer() + "\n";
			msg += "Not passed arguments.\n";
			msg += "See help using: -? or --help \n";    // See in {@link app.Args#isHelp}
			msg += "Exit.\n";
			sod.println(msg);
			return;
		}

		if (len == 1 && cliArgs.isHelp()) {
			sod.println(helpMsg());
			JCommander.newBuilder()
					.addObject(cliArgs)
					.build()
					.usage();
			sod.println(helpExamplesRunMsg());
			return;
		}

		// Checks
		boolean isSetCorrectArgs = false;

		if ( !checkGeneralModeArgs(cliArgs) ) {
			return;
		}

		switch (cliArgs.getMode()) {
			case Args.MODE_SIGN:
				isSetCorrectArgs = checkSignModeArgs(cliArgs);
				break;

			case Args.MODE_DOWNLOAD:
				isSetCorrectArgs = checkDownloadModeArgs(cliArgs);
				break;

			case Args.MODE_METRICS:
				isSetCorrectArgs = checkMetricsModeArgs(cliArgs);
				break;
		}

		if ( !isSetCorrectArgs ) {
			sod.println("What needs to be done ? See examples typed -? or --help \nExit.");
			return;
		}
		// End checks

		CalcSettings calcSettings = new CalcSettings();

		String msg = "";
		if (cliArgs.getMode().equals(Args.MODE_SIGN)) {
			msg += "================================================ \n";
			msg += "= Select SIGN projects in input source ODS  = \n";
			msg += "================================================ \n";

			msg += makeBackupInputODSFile(cliArgs.getInputODS());
			sod.println(msg);
			msg = "";

			int countIDs = signProjects(cliArgs.getInputODS(), cliArgs.getSheetName(), cliArgs.getRange());

			msg += countIDs + " 'names' and 'IDs' saved in input source ODS file. \n";
			msg += "Finish.";
			sod.println(msg);

		} else if (cliArgs.getMode().equals(Args.MODE_DOWNLOAD)) {
			msg += "================================================ \n";
			msg += "= Select DOWNLOAD projects from github hosting = \n";
			msg += "================================================ \n";

			msg += makeBackupInputODSFile(cliArgs.getInputODS());
			sod.println(msg);
			msg = "";

			String dwlCountProjects = downloadProjects(cliArgs.getInputODS(), cliArgs.getSheetName(), cliArgs.getRange(), cliArgs.getIgnoreRows(), cliArgs.getDestination());
			if (dwlCountProjects.equals("-1") || dwlCountProjects.equals("-2")) return;

			msg += "Downloaded " + dwlCountProjects + " projects.\n";
			msg += "Finish.";
			sod.println(msg);

		} else if (cliArgs.getMode().equals(Args.MODE_METRICS)) {
			msg += "================================================ \n";
			msg += "= Select obtain METRICS from already downloaded projects = \n";
			msg += "================================================ \n";

			msg += makeBackupInputODSFile(cliArgs.getInputODS());
			sod.println(msg);
			msg = "";

			int projectsCount = getMetrics(cliArgs.getInputODS(), cliArgs.getInputDir(), cliArgs.getSheetName(), cliArgs.getRange(), calcSettings);

			msg += "Metric values saved to input ODS file.\n";
			msg += "Processed " + projectsCount + " projects. \n";
			msg += "Finish.";
			sod.println(msg);

		}
	}

	private static String makeBackupInputODSFile(String inputODS) {
		String msg = "Create backup input ODS file to: ";
		String bckODSFilePath = Utils.makeBackupSourceFile(new File(inputODS));
		msg += bckODSFilePath;
		return msg;
	}


	private static boolean checkSignModeArgs(Args cliArgs) {
		boolean isCorrectArgMode, isCorrectArgInputODS;

		isCorrectArgMode = checkGeneralModeArgs(cliArgs);
		isCorrectArgInputODS = checkInputODS(cliArgs);

		return isCorrectArgMode && isCorrectArgInputODS;
	}

	private static boolean checkDownloadModeArgs(Args cliArgs) {
		boolean isCorrectArgMode, isCorrectArgInputODS, isCorrectArgDestDir = true;

		isCorrectArgMode = checkGeneralModeArgs(cliArgs);
		isCorrectArgInputODS = checkInputODS(cliArgs);

		if (cliArgs.getDestination() == null) {
			sod.println("Destination path must be passed.");
			isCorrectArgDestDir = false;
		}

		return isCorrectArgMode && isCorrectArgInputODS && isCorrectArgDestDir;
	}

	private static boolean checkMetricsModeArgs(Args cliArgs) {
		boolean isCorrectArgMode, isCorrectArgInputODS, isCorrectArgInputProjDir = true, isCorrectArgSheet = true;

		isCorrectArgMode = checkGeneralModeArgs(cliArgs);
		isCorrectArgInputODS = checkInputODS(cliArgs);

		if (cliArgs.getInputDir() == null) {
			sod.println("Input projects dir must be passed. ");
			isCorrectArgInputProjDir = false;
		}

		if (cliArgs.getSheetName() == null) {
			sod.println("Sheet name must be passed.");
			isCorrectArgSheet = false;
		}

		return isCorrectArgMode && isCorrectArgInputODS && isCorrectArgInputProjDir && isCorrectArgSheet;
	}

	// Internal use only
	private static boolean checkGeneralModeArgs(Args cliArgs) {
		if (cliArgs.getMode() == null) {
			sod.println("Mode must be selected.\nExit.");
			return false;
		}
		return true;
	}

	// Internal use only
	private static boolean checkInputODS(Args cliArgs) {
		if (cliArgs.getInputODS() == null) {
			sod.println("Input source ODS file argument must be passed.");
			return false;
		}
		return true;
	}


	/**
	 * Make sign projects IDs in selected sheet in source ODS file.
	 *
	 * @param srcODS
	 * @param sheetName
	 * @return generated and signed id of projects
	 */
	private static int signProjects(String srcODS, String sheetName, int[] range) {
		ODSProcess odsProcess = new ODSProcess(srcODS, sheetName, range, ODSProcess.ExcludeChecks.ID_NAME);

		Hashtable<Integer, String> names = odsProcess.generateNamesInSheet();
		int countNames = odsProcess.saveNamesToSheet(names);

		Hashtable<Integer, String> ids = odsProcess.generateIDsInSheet();
		int countIDs = odsProcess.saveIDsToSheet(ids);

		if (countNames != countIDs) {
			throw new IllegalStateException("The amount of processed projects not match when generating IDs and Names. \n" +
					"Names: " + countNames + ", " + "Ids: " + countIDs);
		}

		return countIDs;
	}


	// Values of this variables saving between recursive call main() method.
	private static int dwlRow = ODSProcess.START_ROW_PROJECTS;
	private static String prIDCurrDwl = "";
	private static boolean isDwlErr = false;
	private static int dwlErrCount = 0;
	private static String prsDwlErrMsgBuff = "";

	/**
	 * Download projects picked in source ODS file from GitHub hosting .
	 *
	 * @param srcFile    source ODS file where are located URL for each project.
	 * @param sheetName
	 * @param range
	 * @param ignoreRows
	 * @param destDir    destination dir where will be saved downloaded projects.
	 */
	private static String downloadProjects(String srcFile, String sheetName, int[] range, List<Integer> ignoreRows, String destDir){
		// Каталог в котором будут сохранены проекты должен быть пустой.
		File dest = new File(destDir);
		if (!dest.isDirectory()) {  // TODO: 08.02.25 may be use throw new NotFoundException !?
			sod.println("Destination path is not directory.\nExit.");
			return "-1";
		}

		ODSProcess odsProc = new ODSProcess(srcFile, sheetName, range);
		ArrayList<Project> projects = odsProc.getProjectsFromSheet();
		int dwlCount = -1;
		try {
			if (isDwlErr) {
				dwlRow++;
				isDwlErr = false;
			}
			while (dwlRow <= odsProc.getRealLastProjectsRowFromSheet()) {
				Project pr = projects.get(dwlRow - ODSProcess.START_ROW_PROJECTS);
				prIDCurrDwl = pr.getId();
				if (ignoreRows.contains(dwlRow)) {
					sod.println("-----------------------------------------------");
					sod.println("!!! Ignoring row: " + dwlRow + ", ID: " + pr.getId() + " !!!");
					sod.println("-----------------------------------------------");
					continue;
				}
				sod.println("-------");  // delimiter line in start project downloading

				dwlCount = dwlRow - ODSProcess.START_ROW_PROJECTS + 1;  // Starting from number 1.
				sod.println("Download project ID: " + pr.getId() + " (" + dwlCount + " / " + projects.size() + ")");
				new GitHubDownloader().downloadGitRepo(pr.getId(), pr.getURL(), destDir);
				// After thrown exception in GitHubDownloader - below code not call !!!

				dwlRow++;
				sod.println("-------");  // delimiter line in end project downloading
			}
			// Wrong dwlRow++  caused in last iteration.
			// This need for return actual numbers of downloaded project for prevent recursion call main and then this method.
			if (dwlRow > odsProc.getRealLastProjectsRowFromSheet()) dwlRow--;

		} catch (GitAPIException e) { // catch TransportException
			isDwlErr = true;
			dwlErrCount++;

			if(dwlErrCount == 1) prsDwlErrMsgBuff = "Not downloaded projects are: \n";
			prsDwlErrMsgBuff += "Row: " + dwlRow + ", project ID: " + prIDCurrDwl + "\n";

			sod.println("~~~~ Downloading project ID: " + prIDCurrDwl + " caused error. ~~~~");
			sod.println("-------");

			main(argv);
		}

		// Make action in end download all projects
		if (dwlRow == odsProc.getRealLastProjectsRowFromSheet()) {
			sod.println("-----------------------------------------------");
			sod.println(prsDwlErrMsgBuff);
			sod.println("-----------------------------------------------");
			return String.valueOf(dwlCount);
		}

		return "-"; // not call.
	}

	/**
	 * @param srcFile      absolute path to input source ODS file
	 * @param srcProjDir   absolute path to analyzing directory contained projects
	 * @param sheetName    selected sheet name
	 * @param range
	 * @param calcSettings
	 * @return
	 */
	private static int getMetrics(String srcFile, String srcProjDir, String sheetName, int[] range, CalcSettings calcSettings) {
		Processor proc = new Processor();
		ODSProcess odsProcess = new ODSProcess(srcFile, sheetName, range);

		ArrayList<Project> projects = new ArrayList<>();
		ArrayList<String> projPathsFS = proc.findSrcPathsForAllProjectsInFS(srcProjDir);

		// Call this method only for obtain all IDs in input source ODS file.
		ArrayList<Project> projectsODS = odsProcess.getProjectsFromSheet();

		for (int i = 0; i < projPathsFS.size(); i++) {
			String prPathFS = projPathsFS.get(i); // абс. (полный) путь к отдельному проекту который подлежит анализу.
			System.out.println("FS path = " + prPathFS);

			proc.extractMetricsCK(prPathFS);

			// Имя выходного файла обычно class.csv, но в данном случае СК добавляет еше префикс имени последнего каталога из пути
			// к анализируемому проекту. Например "~/Dwl/PR1/com/manf/src/" , тогда имя файла будет srcclass.csv .
			// Получим это имя файла из пути.
			String destFileNameClassCK = new File(prPathFS).getName() + "class.csv";

			// Получаем полный путь к файлу, созданному СК.
			// Почему-то СК сохраняет выходной CSV файл на уровень выше.
			prPathFS = prPathFS.substring(0, prPathFS.lastIndexOf(File.separatorChar) );

			String pathFileClassCK = prPathFS + File.separatorChar + destFileNameClassCK;
			System.out.println("pathFileClassCK = " + pathFileClassCK);

			/*
			 Получаем ID проекта из полного пути к каталогу который подлежит анализу.
			 Так как в данном режиме указывается только путь к корню каталога, в котором находятся под-каталоги проектов,
			 предполагается что каждый такой каталог назван согласно шаблону ID. То есть имя каталога проекта
			 и есть его ID. Соответственно в выходной CSV файл будет записан этот ID.
			 Данное предположение также основано на том что сначала каждый проект должен иметь свой ID в исходном
			 ODS файле. Если это не так, то должна быть вызвана команда Args.SIGN. После этого проекты загружаются в
			 определенный корневой каталог. Каждому под-каталогу загруженного проекта, автоматически будет присвоено
			 имя его ID из исходного ODS файла.
			*/
			// Для работы данного алгоритма требуется два пути - текущий (i) и следующий (i+1).
			// Сравниваются не одинаковые символы в этих двух путях.
			String projID;
			if (i < projPathsFS.size() - 1) {
				projID = Utils.extractProjectIDFromFS(prPathFS, projPathsFS.get(i + 1));

			} else {
				projID = Utils.extractProjectIDFromFS(prPathFS, projPathsFS.get(0));// last project compared by first.
			}
			sod.println("Process project ID = " + projID);

			Project project = new Project();
			project.setId(projID);  // ID retrieved from FS

			/*
			Проект описанный в исходном ODS файле должен также находится в общем каталоге проектов, подлежащих анализу.
			Только в таком случае возможно получение метрик. ID проекта в исходном ODS должно быть такое же, как и имя каталога
			этого проекта в общем каталоге всех проектов.
			Примечания для дальнейшего пояснения:
			ODS - исходный файл,
			projDir - каталог проекта в общем каталоге проектов на файл. Системе.
			Общий каталог - каталог в котором находятся загруженные ранее проекты (командой DOWNLOAD) и подлежащие анализу (сбору числовых значений метрик).

			Могут быть 3 ситуации:
			1. Полное сопоставление (ODS+ projDir+). Получение метрик будет выполнено т.к. данные о проекте
			находятся в исходном ODS файле и этот проект находится в общем каталоге проектов.
			2. Частичное сопоставление (ODS+ projDir-). Получение метрик НЕ будет выполнено для этого проекта. Проект отсутствует в общем каталоге проектов.
			3. Частичное сопоставление (ODS- projDir+). Получение метрик НЕ будет выполнено для этого проекта. ID проекта отсутствует (или не правильное) в исходном ODS файле.

			Первоочередные - проекты находящиеся в общем каталоге проектов.
			Т.к. прежде всего поиск проекта подлежащего анализу выполняется в общем каталоге проектов, а уже затем
			в исходном ODS файле - будут проанализированы только проекты находящиеся в общем каталоге.
			Если некий такое проект не находится в исходном ODS файле, только тогда будет выведено сообщение о том что
			данный проект не будет проанализирован.
			Это позволит выполнять анализ только нужных, загруженных проектов.
			Например, в исходном ODS файле представлены ссылки по 100 проектам.
			Для первых 50-и - числовые значения метрик получены. Далее загружены еще 10 проектов которые
			в исходном файле имеют номер [51...60]. В общем каталоге находятся эти 10 проектов.
			Соответственно мы запускаем процесс получения числовых значений метрик командой METRICS.
			Будут получены значения метрик и записаны в исходный файл для этих 10-и проектов.
			Соответственно остальные проекты в исходном файле игнорируются.
			*/
			int rowODS = -1;
			for (Project prODS : projectsODS) {
				if (prODS.getId().equals(projID)) {
					rowODS = prODS.getRow();
					break;
				}
			}
			if (rowODS == -1) {
				// Проект может быть не распознан потому что некоторые поля в исходном ODS документе могут быть не указаны.
				// Например, не указано имя для проекта и этого уже достаточно чтобы проект не был распозан.
				// См. метод app.ODSProcess.ProjectRow.isProjectRow
				sod.println("Project " + projID + " not found in input ODS file. This project passed.\n");
				continue;
			}

			// Подсчет числ. знач. метрик для каждого элемента проекта (класса, ...)
			proc.calcMetrics(pathFileClassCK, project, calcSettings);
			projects.add(project);
		}

		// Сохраняем числ. знач. всех метрик для каждого проекта в исходном файле.
		return odsProcess.saveMetricsToSheet(projects);
	}


	private static String helpMsg() {
		String msg = "";
		msg += getAppNameAndVer() + "\n";
		msg += "This tiny command-line (CLI) utility ... .";    // TODO do write help msg
		return msg;
	}

	private static String helpExamplesRunMsg() {
		String msg = "";
		msg += "In next examples show all cycle of obtain numerical values of metrics. \n";
		msg += "Sign (auto-generate) 'names' and 'ids' for projects. Next download this projects. Finally obtain metrics \n";
		msg += "Examples:\n";
		msg += "java -jar ~/ck-metrics-collector-plus.jar -?\n";
		msg += "java -jar ~/ck-metrics-collector-plus.jar -m SIGN -s Sheet3 -i1 ~/Apps/projects.ods \n";
		msg += "java -jar ~/ck-metrics-collector-plus.jar -m DOWNLOAD -s Sheet3 -i1 ~/Apps/projects.ods -d ~/Apps/FileManagers/ \n";
		msg += "java -jar ~/ck-metrics-collector-plus.jar -m METRICS -s Sheet3 -i1 ~/Apps/projects.ods -i2 ~/Apps/FileManagers/ \n";
		return msg;
	}
}
