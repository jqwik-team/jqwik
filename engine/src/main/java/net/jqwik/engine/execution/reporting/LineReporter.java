package net.jqwik.engine.execution.reporting;

public class LineReporter {

	private final StringBuilder builder;

	public LineReporter(StringBuilder builder) {
		this.builder = builder;
	}

	public void addLine(int indent, String line) {
		String indentation = multiply(' ', indent * 2);
		builder.append(String.format("%s%s%n", indentation, line));
	}

	private String multiply(char c, int times) {
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < times; j++) {
			builder.append(c);
		}
		return builder.toString();
	}

	public void addUnderline(int indent, int length) {
		String underline = multiply('-', length);
		addLine(indent, underline);
	}
}
