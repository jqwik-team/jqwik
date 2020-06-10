package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.engine.support.*;

class ObjectValueReport extends ValueReport {

	private Object value;

	ObjectValueReport(Optional<String> header, Object value) {
		super(header);
		this.value = value;
	}

	@Override
	public String compactString() {
		return header.orElse("") + JqwikStringSupport.displayString(value);
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, compactString() + appendix);
	}
}
