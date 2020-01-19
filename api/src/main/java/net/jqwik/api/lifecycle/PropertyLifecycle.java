package net.jqwik.api.lifecycle;

import java.util.function.*;

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
		PropertyExecutionResult execute(PropertyExecutionResult executionResult, PropertyLifecycleContext context);
	}

	@API(status = INTERNAL)
	public static abstract class PropertyLifecycleFacade {
		private static PropertyLifecycle.PropertyLifecycleFacade implementation;

		static {
			implementation = FacadeLoader.load(PropertyLifecycle.PropertyLifecycleFacade.class);
		}

		public abstract void after(Object key, AfterPropertyExecutor afterPropertyExecutor);

		public abstract <T> Store<T> store(String name, Supplier<T> initializer);
	}

	public static void onSuccess(String key, Runnable runnable) {
		AfterPropertyExecutor afterPropertyExecutor = (executionResult, context) -> {
			if (executionResult.getStatus() == PropertyExecutionResult.Status.SUCCESSFUL) {
				runnable.run();
			}
			return executionResult;
		};
		after(key, afterPropertyExecutor);
	}

	public static void onSuccess(Runnable runnable) {
		onSuccess(null, runnable);
	}

	public static void after(AfterPropertyExecutor afterPropertyExecutor) {
		after(null, afterPropertyExecutor);
	}

	public static void after(Object key, AfterPropertyExecutor afterPropertyExecutor) {
		PropertyLifecycleFacade.implementation.after(key, afterPropertyExecutor);
	}

	public static <T> Store<T> store(String name, Supplier<T> initializer) {
		return PropertyLifecycleFacade.implementation.store(name, initializer);
	}

}
