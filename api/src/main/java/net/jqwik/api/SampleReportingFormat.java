package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.1")
public interface SampleReportingFormat extends Comparable<SampleReportingFormat> {

	/**
	 * @param value the value to format
	 * @return true if this format applies
	 */
	boolean appliesTo(Object value);

	/**
	 * Transform the value into one that will be handled by default mechanism,
	 * e.g. a Collection, a String, a Map.
	 *
	 * @param value the value to format
	 * @return the transformed value
	 */
	Object report(Object value);

	/**
	 * @param value the value to format
	 * @return an optional label prepended to a value's report
	 */
	default Optional<String> label(Object value) {
		return Optional.empty();
	}

	/**
	 * @return priority with which to apply this format
	 */
	default int priority() {
		return 0;
	}

	@Override
	@API(status = INTERNAL)
	default int compareTo(SampleReportingFormat other) {
		return -Integer.compare(this.priority(), other.priority());
	}
}
