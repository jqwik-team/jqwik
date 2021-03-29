package net.jqwik.engine.statistics;

import java.util.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatisticsHookTests {

	@Property(tries = 10)
	@AddLifecycleHook(CheckDefaultStatisticsReport.class)
	void defaultReporting(@ForAll int anInt) {
		Statistics.collect(anInt);
	}

	private static class CheckDefaultStatisticsReport extends CheckStatisticsReport {
		@Override
		void check(Reporter reporter) {
			verify(reporter).publishValue(
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

	private static class CheckTwoFormats extends CheckStatisticsReport {
		@Override
		void check(Reporter reporter) {
			verify(reporter).publishValue(
					contains("first"),
					contains("first format")
			);
			verify(reporter).publishValue(
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

abstract class CheckStatisticsReport implements AroundPropertyHook {

	Reporter reporter = Mockito.mock(Reporter.class);

	@Override
	public int aroundPropertyProximity() {
		// Outside StatisticsHook
		return -100;
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
		context.wrapReporter(ignore -> reporter);
		PropertyExecutionResult result = property.execute();
		check(reporter);
		return result;
	}

	abstract void check(Reporter reporter);
}