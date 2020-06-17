package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

class CollectionValueReport extends ValueReport {

	private static final int MAX_LINE_LENGTH = 100;

	private final List<ValueReport> collection;

	CollectionValueReport(Optional<String> label, List<ValueReport> collection) {
		super(label);
		this.collection = collection;
	}

	@Override
	public String singleLineReport() {
		return label.orElse("") + "[" + singleLineCollection() + "]";
	}

	private String singleLineCollection() {
		return collection.stream().map(ValueReport::singleLineReport).collect(Collectors.joining(", "));
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, label.orElse("") + "[");
		reportCollection(lineReporter, indentLevel + 1);
		lineReporter.addLine(indentLevel, "]" + appendix);
	}

	private void reportCollection(LineReporter lineReporter, int indentLevel) {
		String currentLine = "";
		for (int i = 0; i < collection.size(); i++) {
			ValueReport elementReport = collection.get(i);
			boolean isNotLastElement = i < collection.size() - 1;
			String singleLineElement = elementReport.singleLineReport();
			if (currentLine.length() + singleLineElement.length() + indentLevel * 2 <= MAX_LINE_LENGTH) {
				currentLine += singleLineElement;
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
