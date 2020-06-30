package net.jqwik.engine.execution.reporting;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class SampleReporter {

	private static final int MAX_LINE_LENGTH = 100;

	public static void reportSample(
		StringBuilder reportLines,
		Method propertyMethod,
		List<Object> sample,
		String headline
	) {
		List<String> parameterNames = Arrays.stream(propertyMethod.getParameters())
											.map(Parameter::getName)
											.collect(Collectors.toList());

		Map<String, Object> reports = createReports(sample, parameterNames);
		SampleReporter sampleReporter = new SampleReporter(headline, reports);
		LineReporter lineReporter = new LineReporterImpl(reportLines);
		sampleReporter.reportTo(lineReporter);
	}

	private static Map<String, Object> createReports(final List<Object> sample, final List<String> parameterNames) {
		if (sample.size() != parameterNames.size()) {
			throw new IllegalArgumentException("Number of sample parameters must be equal to number of parameter names");
		}
		LinkedHashMap<String, Object> samples = new LinkedHashMap<>();
		for (int i = 0; i < sample.size(); i++) {
			String parameterName = parameterNames.get(i);
			Object parameter = sample.get(i);
			samples.put(parameterName, parameter);
		}
		return samples;
	}


	private final String headline;
	private final Map<String, Object> reports;

	public SampleReporter(String headline, Map<String, Object> reports) {
		this.reports = reports;
		this.headline = headline;
	}

	void reportTo(LineReporter lineReporter) {
		lineReporter.addLine(0, "");
		reportHeadline(lineReporter);
		reportParameters(lineReporter);
	}

	private void reportParameters(LineReporter lineReporter) {
		for (Map.Entry<String, Object> nameAndValue : reports.entrySet()) {
			String parameterName = nameAndValue.getKey();
			Object parameterValue = nameAndValue.getValue();
			ValueReport sampleReport = createReport(parameterValue);
			if (sampleReport.singleLineLength() + parameterName.length() < MAX_LINE_LENGTH) {
				String line = String.format("%s: %s", parameterName, sampleReport.singleLineReport());
				lineReporter.addLine(1, line);
			} else {
				String line = String.format("%s:", parameterName);
				lineReporter.addLine(1, line);
				sampleReport.report(lineReporter, 2, "");
			}
		}
	}

	private ValueReport createReport(Object value) {
		return ValueReport.of(value);
	}

	private void reportHeadline(LineReporter lineReporter) {
		if (headline == null) {
			return;
		}
		lineReporter.addLine(0, headline);
		lineReporter.addUnderline(0, headline.length());
	}
}
