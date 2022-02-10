package net.jqwik.engine.execution.reporting;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class SampleReporter {

	public static final int MAX_LINE_LENGTH = 100;

	static void reportSample(
		StringBuilder stringBuilder,
		Method propertyMethod,
		List<Object> sample,
		String headline,
		int indentLevel,
		Collection<SampleReportingFormat> sampleReportingFormats
	) {
		Map<String, Object> reports = createSampleReports(propertyMethod, sample);
		SampleReporter sampleReporter = new SampleReporter(headline, reports, sampleReportingFormats);
		LineReporter lineReporter = new BuilderBasedLineReporter(stringBuilder, indentLevel);
		sampleReporter.reportTo(lineReporter);
	}

	public static Map<String, Object> createSampleReports(Method propertyMethod, List<Object> sample) {
		if (sample.size() != propertyMethod.getParameters().length) {
			throw new IllegalArgumentException("Number of sample parameters must be equal to number of parameter names");
		}

		List<String> parameterNames = Arrays.stream(propertyMethod.getParameters())
											.map(Parameter::getName)
											.collect(Collectors.toList());

		return createReports(sample, parameterNames);
	}

	private static Map<String, Object> createReports(List<Object> sample, List<String> parameterNames) {
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
	private final Collection<SampleReportingFormat> sampleReportingFormats;

	SampleReporter(String headline, Map<String, Object> reports, Collection<SampleReportingFormat> sampleReportingFormats) {
		this.reports = reports;
		this.headline = headline;
		this.sampleReportingFormats = sampleReportingFormats;
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
			ValueReport sampleReport = ValueReport.of(parameterValue, sampleReportingFormats);
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

	private void reportHeadline(LineReporter lineReporter) {
		if (headline == null) {
			return;
		}
		lineReporter.addLine(0, headline);
		lineReporter.addUnderline(0, headline.length());
	}
}
