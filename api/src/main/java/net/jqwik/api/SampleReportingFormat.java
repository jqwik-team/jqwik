package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.4.0")
public interface SampleReportingFormat extends Comparable<SampleReportingFormat> {

	/**
	 * Use in {@linkplain SampleReportingFormat#report(Object)}
	 * if you just want to report a plain label and not a String in quotes.
	 *
	 * @param plain String to report
	 * @return an object that will be reported as a plain label
	 */
	static Object plainLabel(String plain) {
		return new Object() {
			@Override
			public String toString() {
				return plain;
			}
		};
	}

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
	 * Higher priorities have precedence.
	 *
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
