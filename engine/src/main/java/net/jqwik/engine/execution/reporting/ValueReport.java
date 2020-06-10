package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

public abstract class ValueReport {

	interface ReportingFormatFinder {
		SampleReportingFormat find(Object value);
	}

	static ValueReport of(Object value) {
		ReportingFormatFinder formatFinder = reportingFormatFinder();
		return of(value, formatFinder);
	}

	static ValueReport of(Object value, ReportingFormatFinder formatFinder) {
		SampleReportingFormat format = formatFinder.find(value);
		Object reportedValue = format.report(value);
		if (reportedValue instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) reportedValue;
			List<ValueReport> reportCollection =
				collection
					.stream()
					.map(element -> of(element, formatFinder))
					.collect(Collectors.toList());
			return new CollectionValueReport(format.sampleTypeHeader(), reportCollection);
		}
		return new ObjectValueReport(format.sampleTypeHeader(), reportedValue);
	}

	private static ReportingFormatFinder reportingFormatFinder() {
		List<SampleReportingFormat> formats = new ArrayList<>(RegisteredSampleReportingFormats.getReportingFormats());
		Collections.sort(formats);
		return targetValue -> formats.stream()
									 .filter(format -> format.applyToType(targetValue.getClass()))
									 .findFirst().orElse(new NullReportingFormat());
	}

	final Optional<String> header;

	protected ValueReport(Optional<String> header) {
		this.header = header;
	}

	int compactLength() {
		return compactString().length();
	}

	abstract String compactString();

	abstract void report(LineReporter lineReporter, int indentLevel, String appendix);
}
