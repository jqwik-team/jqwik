package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public abstract class ValueReport {

	interface ReportingFormatFinder {
		SampleReportingFormat find(Object value);
	}

	public static ValueReport of(Object value) {
		ReportingFormatFinder formatFinder = reportingFormatFinder();
		return of(value, formatFinder);
	}

	static ValueReport of(Object value, ReportingFormatFinder formatFinder) {
		SampleReportingFormat format = formatFinder.find(value);
		Object reportedValue = format.report(value);
		if (reportedValue instanceof Collection) {
			//noinspection unchecked
			return createCollectionReport(format.label(value), (Collection<Object>) reportedValue, formatFinder);
		}
		if (reportedValue instanceof Map) {
			//noinspection unchecked
			return createMapReport(format.label(value), (Map<Object, Object>) reportedValue, formatFinder);
		}
		return new ObjectValueReport(format.label(value), reportedValue);
	}

	private static ValueReport createMapReport(
		final Optional<String> label,
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
		return new MapValueReport(label, reportEntries);
	}

	private static ValueReport createCollectionReport(
		Optional<String> label,
		Collection<Object> collection,
		ReportingFormatFinder formatFinder
	) {
		List<ValueReport> reportCollection =
			collection
				.stream()
				.map(element -> of(element, formatFinder))
				.collect(Collectors.toList());
		return new CollectionValueReport(label, reportCollection);
	}

	private static ReportingFormatFinder reportingFormatFinder() {
		List<SampleReportingFormat> formats = new ArrayList<>(RegisteredSampleReportingFormats.getReportingFormats());
		Collections.sort(formats);
		return targetValue ->
			formats.stream()
				   .filter(format -> {
					   try {
						   return format.appliesTo(targetValue);
					   } catch (NullPointerException npe) {
						   return false;
					   }
				   })
				   .findFirst().orElse(new NullReportingFormat());
	}

	final Optional<String> label;

	protected ValueReport(Optional<String> label) {
		this.label = label;
	}

	int singleLineLength() {
		return singleLineReport().length();
	}

	public abstract String singleLineReport();

	public abstract void report(LineReporter lineReporter, int indentLevel, String appendix);
}
