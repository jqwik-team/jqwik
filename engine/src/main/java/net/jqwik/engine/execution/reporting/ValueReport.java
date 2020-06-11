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
			//noinspection unchecked
			return createCollectionReport(format, (Collection<Object>) reportedValue, formatFinder);
		}
		if (reportedValue instanceof Map) {
			//noinspection unchecked
			return createMapReport(format, (Map<Object, Object>) reportedValue, formatFinder);
		}
		return new ObjectValueReport(format.header(value), reportedValue);
	}

	private static ValueReport createMapReport(
		final SampleReportingFormat format,
		final Map<Object, Object> map,
		final ReportingFormatFinder formatFinder
	) {
		List<Map.Entry<ValueReport, ValueReport>> reportEntries =
			map.entrySet()
				.stream()
				.map(entry -> {
					ValueReport keyReport = of(entry.getKey(), formatFinder);
					ValueReport valueReport = of(entry.getValue(), formatFinder);
					return new Map.Entry<ValueReport, ValueReport>() {
						@Override
						public ValueReport getKey() {
							return keyReport;
						}

						@Override
						public ValueReport getValue() {
							return valueReport;
						}

						@Override
						public ValueReport setValue(ValueReport value) {
							throw new UnsupportedOperationException();
						}
					};
				})
				.collect(Collectors.toList());
		return new MapValueReport(format.header(map), reportEntries);
	}

	private static ValueReport createCollectionReport(
		SampleReportingFormat format,
		Collection<Object> collection,
		ReportingFormatFinder formatFinder
	) {
		List<ValueReport> reportCollection =
			collection
				.stream()
				.map(element -> of(element, formatFinder))
				.collect(Collectors.toList());
		return new CollectionValueReport(format.header(collection), reportCollection);
	}

	private static ReportingFormatFinder reportingFormatFinder() {
		List<SampleReportingFormat> formats = new ArrayList<>(RegisteredSampleReportingFormats.getReportingFormats());
		Collections.sort(formats);
		return targetValue -> formats.stream()
									 .filter(format -> format.appliesTo(targetValue))
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
