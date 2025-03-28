package calc;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MetricsCollector {

	private final File f;

	public MetricsCollector(File f) {
		this.f = f;
	}

	public void prepareCSV() {
		boolean isPassAnonymousClasses = true;
		Reader in;
		try {
			in = new FileReader(f);
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

				if (isPassAnonymousClasses && classColumn.contains("$Anonymous")) {
					System.out.println("! Anonymous: " + classColumn);
					countAnonymous++;
					continue;
				}

				// todo - wrong count
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
			}
			System.out.println("==================================");
			System.out.println("[   Summary metrics of project   ]");
			System.out.println("==================================");
			System.out.println("countAnonymous = " + countAnonymous);
			System.out.println("sumNC = " + sumNC);
			System.out.println("NoM (number of methods) = " + sumNoM);
			System.out.println("sumDIT = " + sumDIT);
			System.out.println("sumWMC = " + sumWMC);
			System.out.println("sumRFC = " + sumRFC);
			System.out.println("sumCBO = " + sumCBO);
			System.out.println("sumLOC = " + sumLOC);
			System.out.println("-------");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}




}
