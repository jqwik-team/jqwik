package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

public class StreamReportingFormat implements SampleReportingFormat {
	@Override
	public boolean appliesTo(Object value) {
		return value instanceof ReportableStream;
	}

	@Override
	public Object report(Object value) {
		@SuppressWarnings("rawtypes")
		ReportableStream stream = (ReportableStream) value;
		return stream.values();
	}

	@Override
	public Optional<String> label(Object value) {
		return Optional.of("Stream.of ");
	}
}
