package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public class PropertyLifecycle {

	@FunctionalInterface
	public interface AfterPropertyExecutor {
		PropertyExecutionResult execute(PropertyExecutionResult executionResult, PropertyLifecycleContext context) throws Throwable;
	}

	@API(status = INTERNAL)
	public static abstract class PropertyLifecycleFacade {
		private static PropertyLifecycle.PropertyLifecycleFacade implementation;

		static {
			implementation = FacadeLoader.load(PropertyLifecycle.PropertyLifecycleFacade.class);
		}

		public abstract void after(AfterPropertyExecutor afterPropertyExecutor);
	}

	public static void after(AfterPropertyExecutor afterPropertyExecutor) {
		PropertyLifecycleFacade.implementation.after(afterPropertyExecutor);
	}
}
