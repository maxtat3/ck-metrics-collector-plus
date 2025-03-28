import calc.MetricsCollector;
import org.junit.Test;

import java.io.File;

public class MetricsCollectorTest {

	@Test
	public void testPrepareCSV() {
		File f = new File("src/test/resources/07_class.csv");
//		boolean isf = f.isFile();
//		System.out.println("isf = " + isf);

		new MetricsCollector(f).prepareCSV();
	}
}