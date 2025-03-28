package app;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Standard output (Std.out) interceptor
 */
public class CKStdOutInterceptor extends PrintStream {

	public CKStdOutInterceptor(OutputStream out) {
		super(out);
	}

	@Override
	public void print(String s) {
		super.print(s);
		if (s.toLowerCase().contains("extracted")) {
			System.out.println("+");
		}

	}
}
