package net.jqwik.engine.execution.reporting;

public interface ValueReport {

	static ValueReport of(Object value, int maxLineLength) {
		return new ObjectValueReport(value);
	}

	int compactLength();

	String compactString();

	void report(LineReporter lineReporter, int indentLevel);
}
