package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

class TupleValueReport extends ValueReport {

	private static final int MAX_LINE_LENGTH = 100;

	private final List<ValueReport> tupleReports;

	TupleValueReport(Optional<String> label, List<ValueReport> tupleReports) {
		super(label);
		this.tupleReports = tupleReports;
	}

	@Override
	public String singleLineReport() {
		return label.orElse("") + "(" + singleLineReports() + ")";
	}

	private String singleLineReports() {
		return tupleReports.stream().map(this::singleLineReport).collect(Collectors.joining(", "));
	}

	private String singleLineReport(ValueReport report) {
		return report.singleLineReport();
	}

	@Override
	public void report(LineReporter lineReporter, int indentLevel, String appendix) {
		lineReporter.addLine(indentLevel, label.orElse("") + "(");
		reportTupleReports(lineReporter, indentLevel + 1);
		lineReporter.addLine(indentLevel, ")" + appendix);
	}

	private void reportTupleReports(LineReporter lineReporter, int indentLevel) {
		for (int i = 0; i < tupleReports.size(); i++) {
			boolean isNotLast = i < tupleReports.size() - 1;
			ValueReport reportEntry = tupleReports.get(i);
			String optionalComma = isNotLast ? ", " : "";
			String singleLineEntry = singleLineReport(reportEntry);
			if (singleLineEntry.length() + indentLevel * 2 <= MAX_LINE_LENGTH) {
				lineReporter.addLine(indentLevel, singleLineEntry + optionalComma);
			} else {
				reportEntry.report(lineReporter, indentLevel, optionalComma);
			}
		}
	}
}
