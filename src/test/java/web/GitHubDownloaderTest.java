package web;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.*;
import org.eclipse.jgit.transport.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitHubDownloaderTest {

	private static final List<String> URI_PROJECTS = Arrays.asList(
			"https://github.com/PowerExplorer/PowerFileExplorer",
			"https://github.com/DF1E/SimpleExplorer/",
			"https://github.com/SimpleMobileTools/Simple-File-Manager",
			"https://github.com/Timur-Cheryapov/file-explorer/",
			"https://github.com/codingbychanche/FileDialogTool"
	);

	private static final String CURR_DIR = System.getProperty("user.dir") + File.separator;
	private static final String DEST_TMP_DIR;

	static {
		try {
			DEST_TMP_DIR = Files.createTempDirectory("dwlGitRepos").toFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

//+++
	@Test
	public void testDownloadGitRepo_1repo() {
 		try {
		    new GitHubDownloader().downloadGitRepo("PowerFileExplorer", URI_PROJECTS.get(0), DEST_TMP_DIR);
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}

		 // Make test
		ArrayList<String> dirsInFS = new ArrayList<>();
		new File(DEST_TMP_DIR).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) dirsInFS.add(name);
				return true;
			}
		});

		Assert.assertTrue( dirsInFS.contains("PowerFileExplorer") );
	}

//	+++
	@Test
	public void testDownloadGitRepos_5repos() {
		try {
			for (String uri : URI_PROJECTS) {
				new GitHubDownloader().downloadGitRepo(Paths.get(uri).getFileName().toString(), uri, DEST_TMP_DIR);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Make tests
		ArrayList<String> dirsInFS = new ArrayList<>();
		new File(DEST_TMP_DIR).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) dirsInFS.add(name);
				return true;
			}
		});

		Assert.assertTrue( dirsInFS.contains("PowerFileExplorer") );
		Assert.assertTrue( dirsInFS.contains("SimpleExplorer") );
		Assert.assertTrue( dirsInFS.contains("Simple-File-Manager") );
		Assert.assertTrue( dirsInFS.contains("file-explorer") );
		Assert.assertTrue( dirsInFS.contains("FileDialogTool") );
	}


	@AfterClass
	public static void removeDestDir() {
		System.out.println("Remove tmp destination dir");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		rmDir(new File(DEST_TMP_DIR));
	}

	// See: https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
	private static void rmDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (! Files.isSymbolicLink(f.toPath())) {
					rmDir(f);
				}
			}
		}
		file.delete();
	}



//	@Test
//	public void testDebug() {
//
//	}

}