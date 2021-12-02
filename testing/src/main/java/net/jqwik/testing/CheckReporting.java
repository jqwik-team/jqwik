package net.jqwik.testing;

import org.jetbrains.annotations.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@NonNullApi
public abstract class CheckReporting implements AroundPropertyHook {

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

	public abstract void check(@NotNull Reporter mockReporter);
}
