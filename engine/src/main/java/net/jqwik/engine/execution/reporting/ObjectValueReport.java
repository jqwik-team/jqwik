package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.engine.support.*;

class ObjectValueReport extends ValueReport {

	private List<String> lines;

	ObjectValueReport(Optional<String> header, Object value) {
		super(header);
		this.lines = toStringLines(value);
	}

	private List<String> toStringLines(Object value) {
		String s = JqwikStringSupport.displayString(value);
		return JqwikStringSupport.toLines(s);
	}

	@Override
	public String singleLineReport() {
		return label.orElse("") + String.join(" ", lines);
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		label.ifPresent(headerString -> lineReporter.addLine(indentLevel, headerString));
		int linesIndentLevel = label.map(ignore -> indentLevel + 1).orElse(indentLevel);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			boolean isNotLastLine = i < lines.size() - 1;
			String optionalAppendix = isNotLastLine ? "" : appendix;
			lineReporter.addLine(linesIndentLevel, line + optionalAppendix);
		}
	}
}
