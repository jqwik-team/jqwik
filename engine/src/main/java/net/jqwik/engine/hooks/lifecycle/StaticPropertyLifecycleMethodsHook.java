package net.jqwik.engine.hooks.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.api.lifecycle.PropertyLifecycle.*;
import net.jqwik.engine.hooks.*;

public class StaticPropertyLifecycleMethodsHook implements AroundPropertyHook, ApplyToChildren {

	private static final Object IDENTIFIERS_STORE_ID = Tuple.of(AfterPropertyExecutor.class, "identifiers");
	private static final Object ORDER_STORE_ID = Tuple.of(AfterPropertyExecutor.class, "order");

	public static void addAfterPropertyExecutor(Object identifier, AfterPropertyExecutor afterPropertyExecutor) {
		Store<Set<PropertyExecutorIdentifier>> identifiers =
			Store.get(StaticPropertyLifecycleMethodsHook.IDENTIFIERS_STORE_ID);
		Store<List<AfterPropertyExecutor>> executorsInOrder =
			Store.get(StaticPropertyLifecycleMethodsHook.ORDER_STORE_ID);
		PropertyExecutorIdentifier executorKey = new PropertyExecutorIdentifier(identifier, afterPropertyExecutor.getClass());

		identifiers.update(setOfIdentifiers -> {
			if (setOfIdentifiers.contains(executorKey)) {
				return setOfIdentifiers;
			}
			setOfIdentifiers.add(executorKey);
			executorsInOrder.update(list -> {
				list.add(afterPropertyExecutor);
				return list;
			});
			return setOfIdentifiers;
		});
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		// Identifiers and executors cannot be in Map because order of entry must be kept
		Store<Set<AfterPropertyExecutor>> identifiers =
			Store.create(
				IDENTIFIERS_STORE_ID, Store.Lifespan.PROPERTY,
				HashSet::new
			);
		Store<List<AfterPropertyExecutor>> executorsInOrder =
			Store.create(
				ORDER_STORE_ID, Store.Lifespan.PROPERTY,
				ArrayList::new
			);
		PropertyExecutionResult executionResult = property.execute();
		for (AfterPropertyExecutor afterPropertyExecutor : executorsInOrder.get()) {
			try {
				executionResult = afterPropertyExecutor.execute(executionResult, context);
			} catch (Throwable throwable) {
				executionResult = executionResult.mapToFailed(throwable);
			}
		}
		return executionResult;
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.STATIC_PROPERTY_LIFECYCLE_PROXIMITY;
	}

	static class PropertyExecutorIdentifier {

		private final Object key;
		private final Class<? extends AfterPropertyExecutor> aClass;

		public PropertyExecutorIdentifier(Object key, Class<? extends AfterPropertyExecutor> aClass) {
			this.key = key;
			this.aClass = aClass;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PropertyExecutorIdentifier that = (PropertyExecutorIdentifier) o;
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
