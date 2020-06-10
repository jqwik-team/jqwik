package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

class CollectionValueReport extends ValueReport {

	private static final int MAX_LINE_LENGTH = 100;

	private final List<ValueReport> collection;

	CollectionValueReport(Optional<String> header, List<ValueReport> collection) {
		super(header);
		this.collection = collection;
	}

	@Override
	String compactString() {
		return header.orElse("") + "[" + compactCollection() + "]";
	}

	private String compactCollection() {
		return collection.stream().map(ValueReport::compactString).collect(Collectors.joining(", "));
	}

	@Override
	void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, header.orElse("") + "[");
		reportCollection(lineReporter, indentLevel + 1);
		lineReporter.addLine(indentLevel, "]" + appendix);
	}

	private void reportCollection(LineReporter lineReporter, int indentLevel) {
		String currentLine = "";
		for (int i = 0; i < collection.size(); i++) {
			ValueReport elementReport = collection.get(i);
			boolean isNotLastElement = i < collection.size() - 1;
			String compactElement = elementReport.compactString();
			if (currentLine.length() + compactElement.length() + indentLevel * 2 <= MAX_LINE_LENGTH) {
				currentLine += compactElement;
				if (isNotLastElement) {
					currentLine += ", ";
				}
			} else {
				if (!currentLine.isEmpty()) {
					lineReporter.addLine(indentLevel, currentLine);
					i = i - 1;
				} else {
					String optionalComma = isNotLastElement ? "," : "";
					elementReport.report(lineReporter, indentLevel, optionalComma);
				}
				currentLine = "";
			}
		}
		if (!currentLine.isEmpty()) {
			lineReporter.addLine(indentLevel, currentLine);
		}
	}
}
