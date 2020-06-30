package net.jqwik.engine.execution.reporting;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;

public class DefaultReporter implements Reporter {

	private final BiConsumer<TestDescriptor, ReportEntry> listener;
	private final TestDescriptor descriptor;

	public DefaultReporter(BiConsumer<TestDescriptor, ReportEntry> listener, TestDescriptor descriptor) {
		this.listener = listener;
		this.descriptor = descriptor;
	}

	@Override
	public void publishValue(String key, String value) {
		publish(ReportEntry.from(key, value));
	}

	@Override
	public void publishReport(String key, Object object) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void publishReports(String key, Map<String, Object> objects) {
		publish(ReportEntry.from(key, buildReport(objects)));
	}

	private String buildReport(Map<String, Object> objects) {
		SampleReporter sampleReporter = new SampleReporter(null, objects);
		StringBuilder stringBuilder = new StringBuilder();
		LineReporter lineReporter = new LineReporterImpl(stringBuilder);
		sampleReporter.reportTo(lineReporter);
		removeTrailingNewLine(stringBuilder);
		return stringBuilder.toString();
	}

	private void publish(ReportEntry entry) {
		listener.accept(descriptor, entry);
	}

	private void removeTrailingNewLine(final StringBuilder stringBuilder) {
		int lastNewLine = stringBuilder.lastIndexOf(String.format("%n"));
		if (lastNewLine + 1 == stringBuilder.length()) {
			stringBuilder.replace(lastNewLine, lastNewLine + 1, "");
		}
	}

}
