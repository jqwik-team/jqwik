package net.jqwik.engine.execution.reporting;

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
		listener.accept(descriptor, ReportEntry.from(key, value));
	}

	@Override
	public void publishReport(final String key, final Object object) {

	}
}
