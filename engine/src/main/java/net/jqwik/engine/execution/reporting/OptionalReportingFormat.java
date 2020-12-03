package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;

public class OptionalReportingFormat implements SampleReportingFormat {
	@Override
	public boolean appliesTo(Object value) {
		return value instanceof Optional;
	}

	@Override
	public Object report(Object value) {
		Optional<?> optional = (Optional<?>) value;

		return Collections.singletonList(optional.orElse(null));
	}

	@Override
	public Optional<String> label(Object value) {
		return Optional.of("Optional");
	}


}
