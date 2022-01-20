package net.jqwik.engine.execution.reporting;

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
		publish(ReportEntry.from(key, buildReport(object)));
	}

	private String buildReport(Object object) {
		StringBuilder stringBuilder = new StringBuilder();

		ValueReport sampleReport = ValueReport.of(object, getSampleReportingFormats());
		int lengthOfTimestamp = 35;
		if (sampleReport.singleLineLength() < SampleReporter.MAX_LINE_LENGTH - lengthOfTimestamp) {
			String line = sampleReport.singleLineReport();
			stringBuilder.append(line);
		} else {
			stringBuilder.append(String.format("%n"));
			LineReporter lineReporter = new BuilderBasedLineReporter(stringBuilder, 0);
			sampleReport.report(lineReporter, 1, "");
		}
		removeTrailingNewLine(stringBuilder);
		return stringBuilder.toString();
	}

	@Override
	public void publishReports(String key, Map<String, Object> objects) {
		publish(ReportEntry.from(key, buildReports(objects)));
	}

	private Collection<SampleReportingFormat> getSampleReportingFormats() {
		return RegisteredSampleReportingFormats.getReportingFormats();
	}

	private String buildReports(Map<String, Object> reports) {
		SampleReporter sampleReporter = new SampleReporter(null, reports, getSampleReportingFormats());
		StringBuilder stringBuilder = new StringBuilder();
		LineReporter lineReporter = new BuilderBasedLineReporter(stringBuilder, 0);
		sampleReporter.reportTo(lineReporter);
		removeTrailingNewLine(stringBuilder);
		return stringBuilder.toString();
	}

	private void publish(ReportEntry entry) {
		listener.accept(descriptor, entry);
	}

	private void removeTrailingNewLine(StringBuilder stringBuilder) {
		int lastNewLine = stringBuilder.lastIndexOf(String.format("%n"));
		if (lastNewLine + 1 == stringBuilder.length()) {
			stringBuilder.replace(lastNewLine, lastNewLine + 1, "");
		}
	}

}
