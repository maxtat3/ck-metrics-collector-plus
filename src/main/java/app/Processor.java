package app;

import com.github.mauricioaniche.ck.Runner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import pojo.Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

// TODO: 15.03.25 may be rn to MetricsProc
public class Processor {

	private CalcSettings calcSettings;


	public Processor() {
	}

	public Processor(CalcSettings calcSettings) {
		this.calcSettings = calcSettings;
	}


	// COLLECT METRICS
	// Collect metrics from already downloaded projects

	// 1.
	// arg: --mode METRICS
	// arg: --input /home/user/Download/Projects/
	// arg: --sheet-name FileManagers
	// arg: --range
	// arg: destination

//	public List<String> listFilesUsingFileWalk(String dir, int depth) throws IOException {
//		try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
//			return stream
//					.filter(Files::isDirectory)
//					.map(Path::getFileName)
//					.map(Path::toString)
//					.collect(Collectors.toList());
//		}
//	}

//	public class MyFileVisitor extends SimpleFileVisitor<Path> {
//		String partOfName;
//		String partOfContent;
//		ArrayList foundFiles = new ArrayList();
//
//		@Override
//		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//			boolean containsName = true;
//			if (partOfName != null && !file.getFileName().toString().contains(partOfName))
//				containsName = false;
//
//			String content = new String(Files.readAllBytes(file));
//			boolean containsContent = true;
//			if (partOfContent != null && !content.contains(partOfContent))
//				containsContent = false;
//
//			if (containsName && containsContent)
//				foundFiles.add(file);
//
//			return FileVisitResult.CONTINUE;
//		}
//	}

	private ArrayList<String> srcPaths = new ArrayList<>();





	// TODO: 13.03.25 may be move to UtilsFS
	// Метод находит путь к каталогу (на диске) с исходными файлами в контексте Андроид приложений для каждого проекта.
	// Такой путь обычно заканчивается последовательностью каталогов: "src/main/java" .
	// Поэтому для каждого проекта такой путь должен быть одинаковый.
	// endpointProjects - path where stored all will be analysed projects. Example: ~/Dwl/
	// ret absolute paths to projects. Example: 0 - ~/Dwl/PR1/master/endpointProjects/, 1 - ~/Dwl/PR2/com/app/endpointProjects/, 2 - ~/Dwl/PR3/endpointProjects/, ...
	public ArrayList<String> findSrcPathsForAllProjectsInFS(String endpointProjects)  {
		System.out.println("endpointProjects = " + endpointProjects);
		System.out.println("new File(endpointProjects).getAbsolutePath() = " + new File(endpointProjects).getAbsolutePath());
		File[] dirs = new File(endpointProjects).getAbsoluteFile().listFiles();
		assert dirs != null;
		Arrays.sort(dirs);
		try {
			for (File dir : dirs) {
				visitor(dir.getCanonicalPath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return srcPaths;
	}

	private void visitor(String src)  {
		Path startPath = Paths.get(src);
		MyDirVisitor visitor = new MyDirVisitor();
		try {
			Files.walkFileTree(startPath, visitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private class MyDirVisitor implements FileVisitor<Path> {
//		private final String DEST = "src/main/java";
		private final String DEST = "src";

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
			String dirPath = dir.getParent().toString();
			if (dirPath.endsWith(DEST) || dir.endsWith(DEST + "/")) {
//				System.out.println("Main source files dir found. Path=[" + dirPath + "]");
				srcPaths.add(dirPath);
				return FileVisitResult.TERMINATE;
			}
			return FileVisitResult.CONTINUE;
		}
	}

	/**
	 * Run CK for extract metrics from given project. <br>
	 * Results extracted to class.csv file.
	 * <p>
	 * Аргументы передаваемые в CK (Класс Runner): <br>
	 * arg0 (String): <project dir> - каталог, в котором CK может найти весь исходный код для анализа.
	 * Ck будет рекурсивно искать файлы .java. CK может использовать зависимости проекта для повышения точности. <br>
	 * arg1 (boolean): <use jars:true|false> - сообщает CK о необходимости поиска любых файлов .jar в каталоге и использования их для лучшего разрешения типов. <br>
	 * arg2 (int): <max files per partition:0=automatic selection> - размер обрабатываемого пакета. <br>
	 * arg3 (boolean): <variables and fields metrics?:true|false> - нужны ли вам метрики на уровне переменных и полей ? <br>
	 * Вам следует пропустить их, если вам нужны метрики только на уровне класса или метода. <br>
	 * arg4 (String): <output dir> - каталог, в который будет экспортировн csv-файл с метриками из анализируемого проекта. <br>
	 * arg5 ... (String): [ignored directories...] - При желании можно указать любое количество игнорируемых каталогов,
	 * разделенных пробелами (например, build/). По умолчанию .git и все другие скрытые папки игнорируются. <br>
	 * <p>
	 * Некоторые аргументы имееют тип int, boolean, но все же должны быть представлены строками.
	 * В классе Runner эти строки преобразовываются в соответствующие типы.
	 * Все эти аргументы и их порядок такие же как и в непосредственно самом исполняемом jar в виде отдельной программы java -jar ck-x.x.x-SNAPSHOT-jar-with-dependencies.jar .
	 *
	 * @param path - absolute path to analysed project
	 * @see <a href="https://github.com/mauricioaniche/ck?tab=readme-ov-file#how-to-use-the-standalone-version">How to use the standalone version</a>
	 * @see <a href="https://github.com/mauricioaniche/ck?tab=readme-ov-file#how-to-integrate-it-in-my-java-app">How to integrate CK in my Java app</a>
	 */
	public void extractMetricsCK(String path) {
		// Перехват вывода от СК для того чтобы после знать момент времени когда CK получит метрики из проекта и запишет их в файл.
		// Это позволит данной программе передавать в CK следующие проекты без применения фиксированных задержек.
		PrintStream origOut = System.out;
		CKStdOutInterceptor interceptor = new CKStdOutInterceptor(origOut);
		System.setOut(interceptor);

		try {
			Runner.main(new String[]{path, "true", "0", "false", path, "test/", "androidTest/"});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO - возможно метод правильнее назвать sumProjectLevelMetrics ?

	/**
	 * Calculate project metrics values from given analysed project.
	 *
	 * @param calcSettings
	 * @param path путь, где находятся сформированный выходной CSV файл утилитой СК.
	 * @param project
	 * @return
	 */
	public void calcMetrics(String path, Project project, CalcSettings calcSettings) {
		Project.Metrics metrics = null;
		Reader in;

		try {
			in = new FileReader(path);
			Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
					.setHeader() // Header can be parsed automatically from the input file.
					.setSkipHeaderRecord(true) // Sets whether to skip the header record.
					.build()
					.parse(in);

			int countAnonymous = 0;
			int sumWMC = 0, sumNoM = 0, sumCBO = 0, sumRFC = 0, sumDIT = 0, sumNC = 0, sumLOC = 0;

			for (CSVRecord record : records) {
				String fileColumn = record.get("file");
				String classColumn = record.get("class");
				String typeColumn = record.get("type");

				if (calcSettings.isPassAnonymousClasses() && classColumn.contains("$Anonymous")) {
//					System.out.println("! Anonymous: " + classColumn);
					countAnonymous++;
					continue;
				}

				// TODO - wrong count
				// Получаем количество классов в проекте
				if (typeColumn.equals("class")) {
					sumNC += 1;
				}

				sumNoM += Integer.parseInt(record.get("totalMethodsQty"));
				sumDIT += Integer.parseInt(record.get("dit"));
				sumWMC += Integer.parseInt(record.get("wmc"));
				sumRFC += Integer.parseInt(record.get("rfc"));
				sumCBO += Integer.parseInt(record.get("cbo"));
				sumLOC += Integer.parseInt(record.get("loc"));

				metrics = project.new Metrics(sumLOC, sumNC, sumNoM, sumDIT, sumWMC, sumRFC, sumCBO);
				project.setMetrics(metrics);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


// Т.к. мы много проектов за один раз записываем в выходной (destination) файл,
// то передаем список метрик по каждому проекту ArrayList<Project.Metrics metrics> metricsOfProjects
public String writeMetricsToDestCSVFile(String pathToDestDir, ArrayList<Project> projects) {
	String currDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
	String fileName = "projects-metrics_" + currDate + ".csv";
	String pathToDestFile = pathToDestDir + File.separator + fileName;

	String[] HEADERS = {"id", "LOC", "NC", "NoM", "DIT", "WMC", "RFC", "CBO"};
//	CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(HEADERS);
	CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
			.setHeader(HEADERS)
			.build();
	try (
			BufferedWriter writer = Files.newBufferedWriter( Paths.get(pathToDestFile) , StandardCharsets.UTF_8 );
			CSVPrinter printer = new CSVPrinter( writer , csvFormat );
	){
		for (Project pr : projects) {
			printer.printRecord(
					pr.getId(),
					pr.getMetrics().getLOC(),
					pr.getMetrics().getNC(),
					pr.getMetrics().getNoM(),
					pr.getMetrics().getDIT(),
					pr.getMetrics().getWMC(),
					pr.getMetrics().getRFC(),
					pr.getMetrics().getCBO()
			);
		}
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
	return pathToDestFile;
}




//	public List<String> listFilesUsingDirectoryStream(String dir) throws IOException {
//		List<String> dirs = new ArrayList<>();
//		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
//			for (Path path : stream) {
//				if (Files.isDirectory(path)) {
//					dirs.add(path.toString());
//				}
//			}
//		}
//		return dirs;
//	}






	// DOWNLOAD PROJECT FROM GITHUB

	// Open src ODS file


}
