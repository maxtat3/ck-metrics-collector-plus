package pojo;

/**
 * Анализируемый проект.
 */
public class Project {

	/** Row in Sheet in source ODS file */
	private int row;

	/**
	 * Number of project
	 */
	private int num;

	/**
	 * Identifier of project.
	 * Example: FM_FCH_J_15
	 */
	private String id;

	/**
	 * Name of the project
	 */
	private String name;

	private String type;

	private String subType;

	private String language;

	/**
	 * URL of project in GitHub hosting.
	 */
	private String URL;


	public Project() {
	}

	public Project(Metrics metrics) {
		this.metrics = metrics;
	}

	public Project(int row, int num, String id, String name, String type, String subType, String language, String URL) {
		this.row = row;
		this.num = num;
		this.id = id;
		this.type = type;
		this.subType = subType;
		this.name = name;
		this.URL = URL;
		this.language = language;
	}


	public int getRow() {
		return row;
	}

	public int getNum() {
		return num;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}

	public String getLanguage() {
		return language;
	}

	public String getURL() {
		return URL;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}


	/**
	 * Числовые значения метрик данного анализируемого проекта.
	 * Аббревиатуры метрик будут представлены заглавными буквами.
	 */
	public class Metrics {

		/**
		 * Lines Of Code
		 * Обозначение в class.csv:
		 *
		 * Note: CK разделяет классы, внутренние классы и анонимные классы.
		 * LOC — единственная метрика, которая не полностью изолирована от других,
		 * например, если A имеет объявление внутреннего класса B, то LOC(A) = LOC(класс A) + LOC(внутренний класс B).
		 * @see <a href="https://github.com/mauricioaniche/ck#:~:text=Note%3A%20CK%20separates%20classes%2C%20inner%20classes%2C%20and%20anonymous%20classes.">Замечаение по LOC</a>
		 */
		private int LOC;

		/**
		 * Number Classes
		 * Обозначение в class.csv:
		 */
		private int NC;

		/**
		 * Number of Methods - количество методов классе.
		 * Обозначение в class.csv: totalMethodsQty
		 */
		private int NoM;


		/**
		 * Depth of Inheritance Tree
		 * Notation in class.csv: dit
		 */
		private int DIT;

		/**
		 * Weighted Methods per Class
		 * Notation in class.csv: wmc
		 */
		private int WMC;

		/**
		 * Response For a Class
		 * Notation in class.csv: rfc
		 */
		private int RFC;

		/**
		 * Coupling Between Object classes
		 * Notation in class.csv: cbo
		 */
		private int CBO;

		public Metrics(int LOC, int NC, int NoM, int DIT, int WMC, int RFC, int CBO) {
			this.LOC = LOC;
			this.NC = NC;
			this.NoM = NoM;
			this.DIT = DIT;
			this.WMC = WMC;
			this.RFC = RFC;
			this.CBO = CBO;
		}

		public int getLOC() {
			return LOC;
		}

		public int getNC() {
			return NC;
		}

		public int getNoM() {
			return NoM;
		}

		public int getDIT() {
			return DIT;
		}

		public int getWMC() {
			return WMC;
		}

		public int getRFC() {
			return RFC;
		}

		public int getCBO() {
			return CBO;
		}
	}

	private Metrics metrics;

	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	public Metrics getMetrics() {
		return metrics;
	}

}
