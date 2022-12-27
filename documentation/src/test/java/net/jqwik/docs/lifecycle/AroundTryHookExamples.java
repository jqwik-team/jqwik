package net.jqwik.docs.lifecycle;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class AroundTryHookExamples {

	@Property(tries = 10)
	@AddLifecycleHook(FailIfTooSlow.class)
	void sleepingProperty(@ForAll JqwikRandom random) throws InterruptedException {
		Thread.sleep(random.nextInt(101));
	}

}

class FailIfTooSlow implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(
		final TryLifecycleContext context,
		final TryExecutor aTry,
		final List<Object> parameters
	) {
		long before = System.currentTimeMillis();
		TryExecutionResult result = aTry.execute(parameters);
		long after = System.currentTimeMillis();
		long time = after - before;
		if (time >= 100) {
			String message = String.format("%s was too slow: %s ms", context.label(), time);
			return TryExecutionResult.falsified(new AssertionFailedError(message));
		}
		return result;
	}
}
