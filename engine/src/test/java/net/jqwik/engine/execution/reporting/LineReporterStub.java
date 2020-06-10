package net.jqwik.engine.execution.reporting;

import java.util.*;

public class LineReporterStub implements LineReporter {

	final List<String> lines = new ArrayList<>();

	@Override
	public void addLine(int indentLevel, String line) {
		String indentation = LineReporter.multiply(' ', indentLevel * 2);
		this.lines.add(indentation + line.trim());
	}
}
