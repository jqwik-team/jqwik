package net.jqwik.engine.execution.reporting;

class BuilderBasedLineReporter implements LineReporter {

	private final StringBuilder builder;
	private final int baseIndent;

	BuilderBasedLineReporter(StringBuilder builder, int baseIndent) {
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
