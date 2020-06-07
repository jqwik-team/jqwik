package net.jqwik.engine.execution.reporting;

import net.jqwik.engine.support.*;

class ValueReport {
	private Object value;

	public ValueReport(final Object value) {
		this.value = value;
	}

	public int compactLength() {
		return compactString().length();
	}

	public String compactString() {
		return JqwikStringSupport.displayString(value);
	}

	public void report(LineReporter lineReporter, int indent) {
		lineReporter.addLine(indent, compactString());
	}
}
