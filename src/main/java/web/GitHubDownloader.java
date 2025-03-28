package web;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
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

//	private class SimpleProgressMonitor implements ProgressMonitor {
//		@Override
//		public void start(int totalTasks) {
//			System.out.println("Starting work on " + totalTasks + " tasks");
//		}
//
//		@Override
//		public void beginTask(String title, int totalWork) {
//			System.out.println("Start " + title + ": " + totalWork);
//		}
//
//		@Override
//		public void update(int completed) {
//			System.out.print(completed + "-");
//		}
//
//		@Override
//		public void endTask() {
//			System.out.println("Done");
//		}
//
//		@Override
//		public boolean isCancelled() {
//			return false;
//		}
//
//		@Override
//		public void showDuration(boolean b) {
//
//		}
//	}


}
