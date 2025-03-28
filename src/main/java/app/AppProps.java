package app;

import java.io.*;
import java.util.Properties;

public class AppProps {

	public static final String APP_NAME = "Metrics Collector Plus";

	public static final String VER = "version.properties";

	private static Properties props;

//	private static InputStream is = getClass().getResourceAsStream("version.properties");

	public AppProps() {
		// https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(VER);

//		try {
//			String path = Thread.currentThread().getContextClassLoader().getResource(VER).toURI().getPath();
//			System.out.println("path = " + path);
//		} catch (URISyntaxException e) {
//			throw new RuntimeException(e);
//		}

//		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				// ...
//				// ...
//			}
//			reader.close();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

	public static void load(){
//		Path path = Paths.get("version.properties");
//		System.out.println("path.toString() = " + path.toString());
//
//		Path currentRelativePath = Paths.get("");
//		String currDir = currentRelativePath.toAbsolutePath().toString();
//
//		System.out.println("Curr dir = " + currDir);
//		String verPath = currDir + File.separator + "version.properties";
//		System.out.println("verPath = " + verPath);

		props = new Properties();

//		try (InputStream in = getClass().getResourceAsStream("/file.txt");
//		     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
//			// Use resource
//		}



		try {
//			props.load(Files.newInputStream(Paths.get("version.properties")));
			props.load(new FileInputStream( "version.properties" ));
//			props.load(app.AppProps.class.getResourceAsStream(VER));
			props.load(ClassLoader.getSystemClassLoader().getResourceAsStream(VER));

//			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(VER));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}
}
