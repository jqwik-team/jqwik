package net.jqwik.engine.hooks.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.api.lifecycle.PropertyLifecycle.*;

public class StaticPropertyLifecycleMethodsHook implements AroundPropertyHook, PropagateToChildren {

	private static final String EXECUTORS_STORE_NAME = String.format("%s:executors", AfterPropertyExecutor.class);

	public static void addAfterPropertyExecutor(String key, AfterPropertyExecutor afterPropertyExecutor) {
		Store<Map<PropertyExecutorKey, AfterPropertyExecutor>> executors = Store.get(StaticPropertyLifecycleMethodsHook.EXECUTORS_STORE_NAME);
		PropertyExecutorKey executorKey = new PropertyExecutorKey(key, afterPropertyExecutor.getClass());
		executors.update(mapOfExecutors -> {
			if (mapOfExecutors.containsKey(executorKey)) {
				return mapOfExecutors;
			}
			HashMap<PropertyExecutorKey, AfterPropertyExecutor> newMap = new HashMap<>(mapOfExecutors);
			newMap.put(executorKey, afterPropertyExecutor);
			return newMap;
		});
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Store<Map<?, AfterPropertyExecutor>> executors =
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

	static class PropertyExecutorKey {

		private final String key;
		private final Class<? extends AfterPropertyExecutor> aClass;

		public PropertyExecutorKey(String key, Class<? extends AfterPropertyExecutor> aClass) {
			this.key = key;
			this.aClass = aClass;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PropertyExecutorKey that = (PropertyExecutorKey) o;
			if (!Objects.equals(key, that.key)) return false;
			return aClass.equals(that.aClass);
		}

		@Override
		public int hashCode() {
			int result = key != null ? key.hashCode() : 0;
			result = 31 * result + aClass.hashCode();
			return result;
		}
	}
}
