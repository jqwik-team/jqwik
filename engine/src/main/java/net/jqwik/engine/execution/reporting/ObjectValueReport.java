package net.jqwik.engine.execution.reporting;

import java.io.*;
import java.util.*;

import net.jqwik.engine.support.*;

class ObjectValueReport extends ValueReport {

	private List<String> lines;

	ObjectValueReport(Optional<String> header, Object value) {
		super(header);
		this.lines = toStringLines(value);
	}

	private List<String> toStringLines(Object value) {
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(JqwikStringSupport.displayString(value)));
		try {
			String line = reader.readLine();
			while (line != null) {
				if (!line.isEmpty()) {
					lines.add(line);
				}
				line = reader.readLine();
			}
		} catch (IOException cannotHappen) {
			throw new RuntimeException(cannotHappen);
		}
		return lines;
	}

	@Override
	public String compactString() {
		return header.orElse("") + String.join(" ", lines);
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		header.ifPresent(headerString -> lineReporter.addLine(indentLevel, headerString));
		int linesIndentLevel = header.map(ignore -> indentLevel + 1).orElse(indentLevel);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			boolean isNotLastLine = i < lines.size() - 1;
			String optionalAppendix = isNotLastLine ? "" : appendix;
			lineReporter.addLine(linesIndentLevel, line + optionalAppendix);
		}
	}
}
