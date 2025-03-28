package util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class UtilsTest {

	private static final String APPS_LIST_ODS = "src/test/resources/projects-list.ods";

	//	@Ignore
	@Test
	public void makeBackupSourceFile() throws IOException {
		File f = new File(APPS_LIST_ODS);
		String bckFilePath = Utils.makeBackupSourceFile(f);
	}

	@Test
	public void testExtractProjectIDFromFS_1() {
		String path1 = "/home/user/Downloads/CK_metrics/Projects/Java/FM_J_01_AnExplorer/AnExplorer-master/app/src/main/java";
		String path2 = "/home/user/Downloads/CK_metrics/Projects/Java/FM_J_02_XFiles/XFiles-master/app/src/main/java";

		String projectID1 = Utils.extractProjectIDFromFS(path1, path2);
		Assert.assertEquals("FM_J_01_AnExplorer", projectID1);
	}

	@Test
	public void testExtractProjectIDFromFS_2() {
		String path9 = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_09_dmanager/project 9/dmanager-master/app/src/main/java";
		String path1 = "/home/max/Downloads/CK_metrics/Projects/Java/FM_J_01_AnExplorer/AnExplorer-master/app/src/main/java";

		String projectID1 = Utils.extractProjectIDFromFS(path9, path1);
		Assert.assertEquals("FM_J_09_dmanager", projectID1);
	}

	@Test
	public void testExtractProjectIDFromFS_3() {
		String path1 = "/home/user/Downloads/CK_metrics/Projects/Java/FM_J_01_AnExplorer/AnExplorer-master/app/src/main/java";
		String path2 = "/home/user/Downloads/CK_metrics/Projects/Java/FM_J_02_XFiles/XFiles-master/app/src/main/java";

		String projectID1 = Utils.extractProjectIDFromFS(path1, path2);
		Assert.assertEquals("FM_J_01_AnExplorer", projectID1);
	}


	@Test
	public void testConvertDoubleStrToInt_passDoubleIntInStr_correct() {
		int val = Utils.convertDoubleStrToInt("15.0");
		Assert.assertEquals(15, val);
	}

	@Test
	public void testConvertDoubleStrToInt_passDoubleDoubleInStr_correct() {
		int val = Utils.convertDoubleStrToInt("15.3");
		Assert.assertEquals(15, val);
	}

	@Test
	public void testConvertDoubleStrToInt_passIntInStr_correct() {
		int val = Utils.convertDoubleStrToInt("25");
		Assert.assertEquals(25, val);
	}

	@Test
	public void testConvertDoubleStrToInt_passZeroIntInStr_correct() {
		int val = Utils.convertDoubleStrToInt("0");
		Assert.assertEquals(0, val);
	}

	@Test
	public void testConvertDoubleStrToInt_passNegIntInStr_correct() {
		int val = Utils.convertDoubleStrToInt("-5.0");
		Assert.assertEquals(-5, val);
	}

	@Test
	public void testConvertDoubleStrToInt_passEmptyStr() {
		int val = Utils.convertDoubleStrToInt("");
		Assert.assertEquals(25, val);
	}

	@Test(expected = NumberFormatException.class)
	public void testConvertDoubleStrToInt_passTextInStr_wrong() {
		Utils.convertDoubleStrToInt("number");
	}

	@Test(expected = NullPointerException.class)
	public void testConvertDoubleStrToInt_passNull() {
		int val = Utils.convertDoubleStrToInt(null);
		Assert.assertEquals(15, val);
	}


//	===========================================
//      testGetLastNameInPath
//	===========================================
	@Test
	public void testGetLastNameInPath_url_pr1() {
		String s = Utils.getLastNameInPath("https://github.com/1hakr/AnExplorer");
		Assert.assertEquals("AnExplorer", s);
	}

	@Test
	public void testGetLastNameInPath_url_pr2() {
		String s = Utils.getLastNameInPath("https://github.com/DF1E/SimpleExplorer/");
		Assert.assertEquals("SimpleExplorer", s);
	}

	@Test
	public void testGetLastNameInPath_localPath() {
		String s = Utils.getLastNameInPath("/home/user/Downloads/CK_metrics/Projects/");
		Assert.assertEquals("Projects", s);
	}

	@Test
	public void testGetLastNameInPath_url_pr3() {
		String s = Utils.getLastNameInPath("https://github.com/SimpleMobileTools/Simple-File-Manager/");
		Assert.assertEquals("Simple-File-Manager", s);
	}

	@Test
	public void testGetLastNameInPath_url_spaceInEndPath() {
		String s = Utils.getLastNameInPath("https://github.com/mhasan-cmt/Green-File-Explorer/ ");
		Assert.assertEquals("Green-File-Explorer", s);
	}

	@Test
	public void test1() {
		String s = " a ";
		s = s.trim();
		System.out.println("s = " + s);
		Assert.assertEquals("a", s);
	}

//	===========================================
}