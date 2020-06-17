package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

class MapValueReport extends ValueReport {

	private static final int MAX_LINE_LENGTH = 100;

	private final List<Map.Entry<ValueReport, ValueReport>> reportEntries;

	MapValueReport(final Optional<String> label, final List<Map.Entry<ValueReport, ValueReport>> reportEntries) {
		super(label);
		this.reportEntries = reportEntries;
	}

	@Override
	public String singleLineReport() {
		return label.orElse("") + "{" + singleLineEntries() + "}";
	}

	private String singleLineEntries() {
		return reportEntries.stream().map(this::singleLineEntry).collect(Collectors.joining(", "));
	}

	private String singleLineEntry(final Map.Entry<ValueReport, ValueReport> entry) {
		return String.format("%s=%s", entry.getKey().singleLineReport(), entry.getValue().singleLineReport());
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, label.orElse("") + "{");
		reportEntries(lineReporter, indentLevel + 1);
		lineReporter.addLine(indentLevel, "}" + appendix);
	}

	private void reportEntries(LineReporter lineReporter, int indentLevel) {
		for (int i = 0; i < reportEntries.size(); i++) {
			boolean isNotLast = i < reportEntries.size() - 1;
			Map.Entry<ValueReport, ValueReport> reportEntry = reportEntries.get(i);
			String optionalComma = isNotLast ? ", " : "";
			String singleLineEntry = singleLineEntry(reportEntry);
			if (singleLineEntry.length() + indentLevel * 2 <= MAX_LINE_LENGTH) {
				lineReporter.addLine(indentLevel, singleLineEntry + optionalComma);
			} else {
				lineReporter.addLine(indentLevel, String.format("%s=", reportEntry.getKey().singleLineReport()));
				reportEntry.getValue().report(lineReporter, indentLevel + 1, optionalComma);
			}
		}
	}
}
