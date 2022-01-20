package net.jqwik.engine.execution.reporting;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public abstract class ValueReport {

	interface ReportingFormatFinder {
		SampleReportingFormat find(Object value);
	}

	// For Testing only
	static ValueReport of(Object value) {
		List<SampleReportingFormat> availableFormats = RegisteredSampleReportingFormats.getReportingFormats();
		return of(value, availableFormats);
	}

	public static ValueReport of(Object value, Collection<SampleReportingFormat> availableFormats) {
		ReportingFormatFinder formatFinder = formatFinder(availableFormats);
		return of(value, formatFinder);
	}

	private static ValueReport of(Object value, ReportingFormatFinder formatFinder) {
		final Set<Object> visited = visitedSet(Collections.emptySet());
		return of(value, formatFinder, visited);
	}

	private static Set<Object> visitedSet(Set<Object> from) {
		Set<Object> objects = Collections.newSetFromMap(new IdentityHashMap<>());
		objects.addAll(from);
		return objects;
	}

	@SuppressWarnings("unchecked")
	private static ValueReport of(Object value, ReportingFormatFinder formatFinder, Set<Object> visited) {
		SampleReportingFormat format = formatFinder.find(value);
		if (visited.contains(value)) {
			return new CircularDependencyReport(format.label(value), value);
		} else {
			visited.add(value);
		}
		Object reportedValue = format.report(value);
		if (reportedValue instanceof Collection) {
			return createCollectionReport(format.label(value), (Collection<Object>) reportedValue, formatFinder, visited);
		}
		if (reportedValue instanceof Map) {
			return createMapReport(format.label(value), (Map<Object, Object>) reportedValue, formatFinder, visited);
		}
		if (reportedValue instanceof Tuple) {
			return createTupleReport(format.label(value), (Tuple) reportedValue, formatFinder, visited);
		}
		visited.remove(value);
		return new ObjectValueReport(format.label(value), reportedValue);
	}

	private static ValueReport createTupleReport(
		Optional<String> label,
		Tuple tuple,
		ReportingFormatFinder formatFinder,
		final Set<Object> visited
	) {
		List<ValueReport> tupleReports =
			tuple.items()
				 .stream()
				 .map(value -> of(value, formatFinder, visitedSet(visited)))
				 .collect(Collectors.toList());

		return new TupleValueReport(label, tupleReports);
	}

	private static ValueReport createMapReport(
		final Optional<String> label,
		final Map<Object, Object> map,
		final ReportingFormatFinder formatFinder,
		final Set<Object> visited
	) {
		List<Map.Entry<ValueReport, ValueReport>> reportEntries =
			map.entrySet()
			   .stream()
			   .map(entry -> {
				   ValueReport keyReport = of(entry.getKey(), formatFinder, visitedSet(visited));
				   ValueReport valueReport = of(entry.getValue(), formatFinder, visitedSet(visited));
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
		ReportingFormatFinder formatFinder,
		final Set<Object> visited
	) {
		List<ValueReport> reportCollection =
			collection
				.stream()
				.map(element -> of(element, formatFinder, visitedSet(visited)))
				.collect(Collectors.toList());
		return new CollectionValueReport(label, reportCollection);
	}

	private static ReportingFormatFinder formatFinder(Collection<SampleReportingFormat> unsortedFormats) {
		List<SampleReportingFormat> formats = new ArrayList<>(unsortedFormats);
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
