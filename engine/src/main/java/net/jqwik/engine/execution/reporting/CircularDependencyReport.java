package net.jqwik.engine.execution.reporting;

import java.util.*;

class CircularDependencyReport extends ValueReport {

	private final Object value;

	CircularDependencyReport(Optional<String> label, Object value) {
		super(label);
		this.value = value;
	}

	@Override
	public String singleLineReport() {
		return String.format("circular-dependency<%s@%s>", label.orElse(value.getClass().getName()), System.identityHashCode(value));
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, singleLineReport() + appendix);
	}
}
