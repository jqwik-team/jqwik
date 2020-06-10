package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

class MapValueReport extends ValueReport {
	private final List<Map.Entry<ValueReport, ValueReport>> reportEntries;

	MapValueReport(final Optional<String> header, final List<Map.Entry<ValueReport, ValueReport>> reportEntries) {
		super(header);
		this.reportEntries = reportEntries;
	}

	@Override
	String compactString() {
		return header.orElse("") + "{" + compactEntries() + "}";
	}

	private String compactEntries() {
		return reportEntries.stream().map(this::compactEntry).collect(Collectors.joining(", "));
	}

	private String compactEntry(final Map.Entry<ValueReport, ValueReport> entry) {
		return String.format("%s=%s", entry.getKey().compactString(), entry.getValue().compactString());
	}

	@Override
	void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, header.orElse("") + "{");
		reportEntries(lineReporter, indentLevel + 1);
		lineReporter.addLine(indentLevel, "}" + appendix);
	}

	private void reportEntries(LineReporter lineReporter, int indentLevel) {
		for (int i = 0; i < reportEntries.size(); i++) {
			boolean isNotLast = i < reportEntries.size() - 1;
			Map.Entry<ValueReport, ValueReport> reportEntry = reportEntries.get(i);
			String optionalComma = isNotLast ? ", " : "";
			lineReporter.addLine(indentLevel, compactEntry(reportEntry) + optionalComma);
		}
	}
}
