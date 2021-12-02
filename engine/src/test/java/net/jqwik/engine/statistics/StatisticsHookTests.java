package net.jqwik.engine.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatisticsHookTests {

	@Property(tries = 10)
	@AddLifecycleHook(CheckDefaultStatisticsReport.class)
	void defaultReporting(@ForAll int anInt) {
		Statistics.collect(anInt);
	}

	private static class CheckDefaultStatisticsReport extends CheckReporting {
		@Override
		public void check(Reporter mockReporter) {
			verify(mockReporter).publishValue(
					contains("statistics"),
					contains("%")
			);
		}
	}

	@Property(tries = 10)
	@StatisticsReport(label = "first", format = FirstFormat.class)
	@StatisticsReport(label = "second", format = SecondFormat.class)
	@AddLifecycleHook(CheckTwoFormats.class)
	void twoReportingFormats(@ForAll int anInt) {
		Statistics.label("first").collect(anInt);
		Statistics.label("second").collect(anInt);
	}

	private static class CheckTwoFormats extends CheckReporting {
		@Override
		public void check(Reporter mockReporter) {
			verify(mockReporter).publishValue(
					contains("first"),
					contains("first format")
			);
			verify(mockReporter).publishValue(
					contains("second"),
					contains("second format")
			);
		}
	}

	private class FirstFormat implements StatisticsReportFormat {
		@Override
		public List<String> formatReport(List<StatisticsEntry> entries) {
			return Collections.singletonList("first format");
		}
	}

	private class SecondFormat implements StatisticsReportFormat {
		@Override
		public List<String> formatReport(List<StatisticsEntry> entries) {
			return Collections.singletonList("second format");
		}
	}


}

