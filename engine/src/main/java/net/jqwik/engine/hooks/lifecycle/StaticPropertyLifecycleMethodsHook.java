package net.jqwik.engine.hooks.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.api.lifecycle.PropertyLifecycle.*;

public class StaticPropertyLifecycleMethodsHook implements AroundPropertyHook, PropagateToChildren {

	private static final String EXECUTORS_STORE_NAME = String.format("%s:executors", AfterPropertyExecutor.class);

	public static void addAfterPropertyExecutor(AfterPropertyExecutor afterPropertyExecutor) {
		Store<Map<Class<?>, AfterPropertyExecutor>> executors = Store.get(StaticPropertyLifecycleMethodsHook.EXECUTORS_STORE_NAME);
		executors.update(mapOfExecutors -> {
			if (mapOfExecutors.containsKey(afterPropertyExecutor.getClass())) {
				return mapOfExecutors;
			}
			HashMap<Class<?>, AfterPropertyExecutor> newMap = new HashMap<>(mapOfExecutors);
			newMap.put(afterPropertyExecutor.getClass(), afterPropertyExecutor);
			return newMap;
		});
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Store<Map<Class<?>, AfterPropertyExecutor>> executors =
			Store.create(
				Store.Visibility.LOCAL,
				EXECUTORS_STORE_NAME,
				IdentityHashMap::new
			);
		PropertyExecutionResult executionResult = property.execute();
		for (AfterPropertyExecutor afterPropertyExecutor : executors.get().values()) {
			try {
				executionResult = afterPropertyExecutor.execute(executionResult, context);
			} catch (Throwable throwable) {
				executionResult = PropertyExecutionResult.failed(
					throwable,
					executionResult.getSeed().orElse(null),
					executionResult.getFalsifiedSample().orElse(Collections.emptyList())
				);
			}
		}
		return executionResult;
	}

	@Override
	public int aroundPropertyProximity() {
		// Should run shortly before AutoCloseableHook
		return -99;
	}

}
