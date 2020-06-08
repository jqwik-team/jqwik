package net.jqwik.engine.execution.reporting;

public interface LineReporter {

	void addLine(int indentLevel, String line);

	static String multiply(char c, int times) {
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < times; j++) {
			builder.append(c);
		}
		return builder.toString();
	}

	default void addUnderline(int indentLevel, int length) {
		String underline = LineReporter.multiply('-', length);
		addLine(indentLevel, underline);
	}

}
