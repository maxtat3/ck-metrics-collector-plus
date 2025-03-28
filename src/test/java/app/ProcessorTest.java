package app;

import org.junit.Assert;
import org.junit.Test;
import pojo.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProcessorTest {

	@Test
	public void testListFilesUsingFileWalk() throws IOException {
		String src = "/home/max/Downloads/CK_metrics/Projects/Java/";
		Processor proc = new Processor();

//		List<String> set = proc.listFilesUsingFileWalk(src, 3);

//		List<String> dirs = proc.listFilesUsingDirectoryStream(src);
//		File[] files = new File(src).listFiles();
//		Arrays.sort(files);
//		for (File file : files) {
//			System.out.println("file.getCanonicalPath() = " + file.getCanonicalPath());
//		}

//		for (String s : dirs) {
//			System.out.println("s = " + s);
//		}

		ArrayList<String> srcPaths = proc.findSrcPathsForAllProjectsInFS(src);
		for (String path : srcPaths) {
			System.out.println("path = " + path);
		}


//		final String DEST = "app/src/main/java";
////		String s = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_01_AnExplorer/AnExplorer-master/app/src/main/java/android/support";
//		String s = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_01_AnExplorer/AnExplorer-master/app/src/main/java";
////		String s1 = s.substring(0, DEST.length() - 1);
//		Assert.assertTrue(s.endsWith(DEST));
	}


	@Test
	public void testCK_extractMetrics() throws IOException {
//		String projPath = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_02_XFiles/";
//		String projPath = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_04_FileManager/FileManager-master/";
		String projPath = "/home/max/SoftwareMiniProjects/Sandbox/ck-metrics-collector-plus_test-projects-dataset/pr4_javaSE_CodeSeriff/";

		new Processor().extractMetricsCK(projPath);

//		System.out.println("Metrics extracted !");
	}

	@Test
	public void testInnerMetricsInstance() {
		Project pr = new Project();
		pr.setNum(15);
		pr.setId("FM_J_155");
		pr.setType("root");

		System.out.println(pr.getNum() + " | " + pr.getId() + " | " + pr.getType());

		Project.Metrics metrics = pr.new Metrics(1, 2, 5, 17, 7, 11, 12);

		System.out.println(pr.getNum() + " | " + pr.getId() + " | " + pr.getType());
		System.out.println(metrics.getDIT());
	}

//	===========================================
//      testCalcMetrics
//	===========================================
	@Test
	public void testCalcMetrics_pr1() {
		String prPath = "srcProjectsBundle/pr1_Android_FileManager/class.csv";
		CalcSettings settings = new CalcSettings();
		Project pr = new Project();
		new Processor().calcMetrics(prPath, pr, settings);

		Assert.assertEquals(2515, pr.getMetrics().getLOC());
		Assert.assertEquals(118, pr.getMetrics().getNoM());
		Assert.assertEquals(13, pr.getMetrics().getNC());
		Assert.assertEquals(55, pr.getMetrics().getDIT());
		Assert.assertEquals(398, pr.getMetrics().getWMC());
		Assert.assertEquals(214, pr.getMetrics().getCBO());
		Assert.assertEquals(568, pr.getMetrics().getRFC());
	}

	@Test
	public void testCalcMetrics_pr2() {
		String prPath = "srcProjectsBundle/pr2_Android_FileManager/class.csv";
		CalcSettings settings = new CalcSettings();
		Project pr = new Project();
		new Processor().calcMetrics(prPath, pr, settings);

		Assert.assertEquals(1961, pr.getMetrics().getLOC());
		Assert.assertEquals(89, pr.getMetrics().getNoM());
		Assert.assertEquals(13, pr.getMetrics().getNC());
		Assert.assertEquals(29, pr.getMetrics().getDIT());
		Assert.assertEquals(270, pr.getMetrics().getWMC());
		Assert.assertEquals(153, pr.getMetrics().getCBO());
		Assert.assertEquals(382, pr.getMetrics().getRFC());
	}

	@Test
	public void testCalcMetrics_pr3() {
		String prPath = "srcProjectsBundle/pr3_Android_AnExplorer/class.csv";
		CalcSettings settings = new CalcSettings();
		Project pr = new Project();
		new Processor().calcMetrics(prPath, pr, settings);

		Assert.assertEquals(1961, pr.getMetrics().getLOC());
		Assert.assertEquals(89, pr.getMetrics().getNoM());
		Assert.assertEquals(13, pr.getMetrics().getNC());
		Assert.assertEquals(29, pr.getMetrics().getDIT());
		Assert.assertEquals(270, pr.getMetrics().getWMC());
		Assert.assertEquals(153, pr.getMetrics().getCBO());
		Assert.assertEquals(382, pr.getMetrics().getRFC());
	}
//	===========================================

//	private File createTempFile() throws IOException {
//		return createTempPath().toFile();
//	}
//
//	private Path createTempPath() throws IOException {
//		return Files.createTempFile(getClass().getName(), ".csv");
//	}

	@Test
	public void testWriteMetricsToDestCSVFile() throws IOException {
		String destDirPath = System.getProperty("user.dir") + "/src/test/resources/";

		Project pr1 = new Project();
		pr1.setId("FM_J_01");
		Project.Metrics pr1Metrics = new Project().new Metrics(1, 2, 3, 4, 5, 6, 7);
		pr1.setMetrics(pr1Metrics);

		Project pr2 = new Project();
		pr2.setId("FM_J_02");
		Project.Metrics pr2Metrics = new Project().new Metrics(55, 12, 15, 17, 5, 9, 18);
		pr2.setMetrics(pr2Metrics);

		Project pr3 = new Project();
		pr3.setId("FM_J_03");
		Project.Metrics proj3Metrics = new Project().new Metrics(11, 190, 315, 19, 55, 77, 11);
		pr3.setMetrics(proj3Metrics);

		ArrayList<Project> projects = new ArrayList<>();
		projects.add(pr1);
		projects.add(pr2);
		projects.add(pr3);

		String destFilePath = new Processor().writeMetricsToDestCSVFile(destDirPath, projects);

		// Test content file
		File f = new File(destFilePath);
		List<String> lines = Files.readAllLines(Paths.get(f.toString()));
		Assert.assertEquals("id,LOC,NC,NoM,DIT,WMC,RFC,CBO", lines.get(0));
		Assert.assertEquals("FM_J_01,1,2,3,4,5,6,7", lines.get(1));
		Assert.assertEquals("FM_J_02,55,12,15,17,5,9,18", lines.get(2));
		Assert.assertEquals("FM_J_03,11,190,315,19,55,77,11", lines.get(3));

		f.deleteOnExit();

//		System.out.println("createTempFile() = " + createTempFile());
//		String destDirPath = System.getProperty("user.dir") + "/src/test/resources/";

	}


}