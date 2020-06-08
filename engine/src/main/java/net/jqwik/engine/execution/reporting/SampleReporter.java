package net.jqwik.engine.execution.reporting;

import java.util.*;

public class SampleReporter {
	private static final int MAX_LINE_LENGTH = 100;

	private final String headline;
	private final List<Object> sample;
	private final List<String> parameterNames;

	public SampleReporter(String headline, List<Object> sample, List<String> parameterNames) {
		if (sample.size() != parameterNames.size()) {
			throw new IllegalArgumentException("Number of sample parameters must be equal to number of parameter names");
		}
		this.headline = headline;
		this.sample = sample;
		this.parameterNames = parameterNames;
	}

	public void reportTo(LineReporter lineReporter) {
		lineReporter.addLine(0, "");
		reportHeadline(lineReporter);
		reportParameters(lineReporter);
	}

	private void reportParameters(LineReporter lineReporter) {
		for (int i = 0; i < parameterNames.size(); i++) {
			String parameterName = parameterNames.get(i);
			Object parameterValue = sample.get(i);
			ValueReport sampleReport = createReport(parameterValue);
			if (sampleReport.compactLength() + parameterName.length() < MAX_LINE_LENGTH) {
				String line = String.format("%s: %s", parameterName, sampleReport.compactString());
				lineReporter.addLine(1, line);
			} else {
				String line = String.format("%s:", parameterName);
				lineReporter.addLine(1, line);
				sampleReport.report(lineReporter, 2);
			}
		}
	}

	private ValueReport createReport(Object value) {
		return ValueReport.of(value, MAX_LINE_LENGTH);
	}

	private void reportHeadline(LineReporter lineReporter) {
		if (headline == null) {
			return;
		}
		lineReporter.addLine(0, headline);
		lineReporter.addUnderline(0, headline.length());
	}
}
