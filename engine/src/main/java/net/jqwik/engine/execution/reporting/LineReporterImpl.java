package net.jqwik.engine.execution.reporting;

public class LineReporterImpl implements LineReporter {

	private final StringBuilder builder;

	public LineReporterImpl(StringBuilder builder) {
		this.builder = builder;
	}

	@Override
	public void addLine(int indentLevel, String line) {
		String indentation = LineReporter.multiply(' ', indentLevel * 2);
		builder.append(String.format("%s%s%n", indentation, line));
	}

}
