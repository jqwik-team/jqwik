package net.jqwik.docs.lifecycle;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(Timing.TimingHook.class)
public @interface Timing {

	class TimingHook implements AroundPropertyHook, AroundTryHook {

		Store<Long> sumOfTriesTiming = Store.create("sumOfTriesTiming", Lifespan.PROPERTY, () -> 0L);

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			long beforeProperty = System.currentTimeMillis();
			PropertyExecutionResult executionResult = property.execute();
			long afterProperty = System.currentTimeMillis();
			long overallTime = afterProperty - beforeProperty;
			createTimingReport(context.reporter(), context.extendedLabel(), overallTime, executionResult.countTries());
			return executionResult;
		}

		private void createTimingReport(Reporter reporter, String label, long overallTime, int tries) {
			long averageTryTime = sumOfTriesTiming.get() / tries;
			String key = String.format("timing of %s", label);
			String report = String.format("%n\toverall: %d ms%n\taverage try: %d ms", overallTime, averageTryTime);
			reporter.publish(key, report);
		}

		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
			long beforeTry = System.currentTimeMillis();
			try {
				return aTry.execute(parameters);
			} finally {
				long afterTry = System.currentTimeMillis();
				long timeForTry = afterTry - beforeTry;
				sumOfTriesTiming.update(time -> time + timeForTry);
			}
		}
	}
}
