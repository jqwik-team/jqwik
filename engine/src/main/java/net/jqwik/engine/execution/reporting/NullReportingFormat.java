package net.jqwik.engine.execution.reporting;

import java.util.*;

public class NullReportingFormat implements SampleReportingFormat {
	@Override
	// Never used
	public boolean applyToType(final Class<?> valueClass) {
		return true;
	}

	@Override
	public Object report(final Object value) {
		return value;
	}

	@Override
	public Optional<String> sampleTypeHeader() {
		return Optional.empty();
	}
}
