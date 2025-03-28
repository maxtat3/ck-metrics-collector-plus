package app;

public class CalcSettings {

	/**
	 * Пропускать ли подсчет анонимных классов утилитой CK ?
	 * Settings level: CK
	 */
	private boolean isPassAnonymousClasses = false;

	public boolean isPassAnonymousClasses() {
		return isPassAnonymousClasses;
	}

	public void setPassAnonymousClasses(boolean passAnonymousClasses) {
		isPassAnonymousClasses = passAnonymousClasses;
	}
}
