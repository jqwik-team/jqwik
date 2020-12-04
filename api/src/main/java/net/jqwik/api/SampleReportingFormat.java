package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.1")
public interface SampleReportingFormat extends Comparable<SampleReportingFormat> {

	/**
	 * Use in {@linkplain SampleReportingFormat#report(Object)}
	 * if you just want to report all of an object's bean properties as
	 * attributes.
	 *
	 * @param bean Java object with its properties following the bean convention
	 * @return a map with all bean properties in alphabetical order
	 * @deprecated Make an implementation of {@linkplain JavaBeanReportingFormat} instead. To be removed in 1.4.0.
	 */
	@Deprecated
	@API(status = DEPRECATED, since = "1.3.10")
	static Object reportJavaBean(Object bean) {
		return JavaBeanReportingFormat.JavaBeanReportingFormatFacade.implementation.reportJavaBean(
				bean,
				false,
				Collections.emptySet(),
				props -> props
		);
	}

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
