package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

	/**
	 * Check is passed directory in string representation is directory and is existed.
	 *
	 * @param path absolute path to directory
	 */
	public static void isDirExist(String path) {

		Path dirPath = Paths.get(path);

		boolean isDir = Files.isDirectory(dirPath);
		if (isDir) {
			throw new IllegalArgumentException("Path is not directory: " + dirPath);
		}

		boolean isExist = Files.exists(dirPath);
		if (isExist) {
			throw new IllegalArgumentException("Path is not exist: " + dirPath);
		}
	}

	public static String getUserHomeDir() {
		return System.getProperty("user.home") + File.separator;
	}

	/**
	 * Get last file or directory name in path.
	 *
	 * Examples:
	 * "https://github.com/SimpleMobileTools/Simple-File-Manager" -> Simple-File-Manager
	 * "C:\\temp\\TestOutput\\TestFolder\\test_file.txt" -> test_file.txt
	 * "C:\\temp\\TestOutput\\TestFolder" -> TestFolder
	 *
	 * @param path absolute path by which you need to get the last file or directory name.
	 * @return last file or directory name from path
	 */
	public static String getLastNameInPath(String path) {
		path = path.trim();
		return Paths.get(path).getFileName().toString();
	}


	/**
	 * Create backup file from source file.
	 * Destination backup file will be crated in same path as a source file.
	 *
	 * @param src absolute path to source file.
	 * @return absolute path to was created backup file.
	 */
	public static String makeBackupSourceFile(File src) {
		// absolute path fo destination backup file.
		// File.separator  or  File.separatorChar  or  FileSystems.getDefault().getSeparator()
		File backup = new File(src.getAbsoluteFile().getParent() + File.separator + generateBackupFileName(src.getName()));

		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			src = src.getAbsoluteFile();
			sourceChannel = new FileInputStream(src).getChannel();
			destChannel = new FileOutputStream(backup).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			sourceChannel.close();
			destChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return backup.getAbsolutePath();
	}

	/**
	 * Generate backup file name from source file name.
	 * Backup file name returned in next format: sourceName + "_bck_" + date.
	 * Example: <br>
	 * source name: apps-list.ods <br>
	 * backup name: apps-list_bck_2025-01-25_15-09-47.ods <br>
	 *
	 * @param src source file name
	 * @return backup file name.
	 */
	private static String generateBackupFileName(String src) {
		String ext = src.substring(src.lastIndexOf('.') + 1);
		// https://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
		String name = src.substring(0, src.lastIndexOf('.'));
		String currDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
		return name + "_bck_" + currDate + "." + ext;
	}

	public static String generateDesFileName() {
		String currDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
		return "projects-metrics_" + currDate + ".csv";
	}

	/**
	 * Extract unique project ID only from absolute path in files system (FS).
	 * Path 2 using only for search first different symbol in path 1.
	 *
	 * @param path1 path to project 1 in FS
	 * @param path2 path to project 2 in FS
	 * @return project ID only for project 1 path
	 */
	public static String extractProjectIDFromFS(String path1, String path2) {
		path1 = path1.endsWith("/") ? path1 : path1 + "/";

		char[] chp1 = path1.toCharArray();
		char[] chp2 = path2.toCharArray();

		// Путь 2 нужен только для нахождения 1-го неодинакового символа в пути 1.
		// Позиция первого не одинакового символа для проекта 1.
		// Это будет средняя позиция, от которой движение в лево и в право до символа-разделителя.
		int pos = 0;
		for (int i = 0; i < chp1.length; i++) {
			if ( chp1[i] != chp2[i] ) {
				pos = i;
				break;
			}
		}

		// Находим левую границу первого символа в имени Проекта 1
		// Определяем по символу разделителю - двигаясь влево по строке абсолютного пути.
		int i = pos;
		do {
			i--;
		} while (chp1[i] != File.separatorChar);
		int posLB = i;
		posLB++; // Убираем символ-разделитель в начале имени.

		// Находим правую границу последнего символа в имени Проекта 1
		// Определяем по символу разделителю - двигаясь вправо по строке абсолютного пути.
		i = pos;
		do {
			i++;
		} while (chp1[i] != File.separatorChar);
		int posRB = i;

//		System.out.println("pos = " + pos);
//		System.out.println("posLB = " + posLB);
//		System.out.println("posRB = " + posRB);

		return path1.substring(posLB, posRB);
	}

	/**
	 * Converting int presented in double presented in string to primitive type int.
	 * Example: "15.0" -> 15.0 -> 15
	 * This method using for retrieve primitive type int value from cell using SODS library.
	 *
	 * @param val int value presented in double presented in string.
	 * @return primitive type int value
	 */
	public static int convertDoubleStrToInt(Object val) {
		if (val == null) {
			throw new NullPointerException();
		}

		String valStr = String.valueOf(val);
		double valD = Double.parseDouble(valStr);
		int valI = (int) valD;
		return valI;
	}


}
