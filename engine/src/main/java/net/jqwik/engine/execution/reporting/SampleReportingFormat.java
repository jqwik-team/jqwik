package net.jqwik.engine.execution.reporting;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.1")
public interface SampleReportingFormat extends Comparable<SampleReportingFormat> {

	boolean appliesTo(Object value);

	Object report(Object value);

	Optional<String> label(Object value);

	default int priority() {
		return 0;
	}

	@Override
	@API(status = INTERNAL)
	default int compareTo(SampleReportingFormat other) {
		return -Integer.compare(this.priority(), other.priority());
	}
}
