package net.jqwik.engine.execution.reporting;

public class LineReporterImpl implements LineReporter {

	private final StringBuilder builder;
	private final int baseIndent;

	public LineReporterImpl(StringBuilder builder, int baseIndent) {
		this.builder = builder;
		this.baseIndent = baseIndent;
	}

	@Override
	public void addLine(int indentLevel, String line) {
		int effectiveIndent = indentLevel + baseIndent;
		String indentation = LineReporter.multiply(' ', effectiveIndent * 2);
		builder.append(String.format("%s%s%n", indentation, line));
	}

}
