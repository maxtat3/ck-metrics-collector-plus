package web;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.PrintWriter;

public class GitHubDownloader {

	public String downloadGitRepo(String projectID, String URI, String destDir) throws GitAPIException {
		File destDirEndPoint = new File(destDir + File.separator + projectID);

//		https://stackoverflow.com/questions/34616354/java-jgit-getting-clone-progress-percentage
		Git call = Git.cloneRepository()
				.setURI(URI)
				.setDirectory(destDirEndPoint)
				.setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
				.call();

		return call.toString();
	}

}
