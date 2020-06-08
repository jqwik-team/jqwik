package net.jqwik.engine.execution.reporting;

import net.jqwik.engine.support.*;

class ObjectValueReport implements ValueReport {

	private Object value;

	ObjectValueReport(final Object value) {
		this.value = value;
	}

	@Override
	public int compactLength() {
		return compactString().length();
	}

	@Override
	public String compactString() {
		return JqwikStringSupport.displayString(value);
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel) {
		lineReporter.addLine(indentLevel, compactString());
	}
}
