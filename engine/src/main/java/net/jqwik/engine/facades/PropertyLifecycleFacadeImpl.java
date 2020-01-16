package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PropertyLifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.hooks.lifecycle.*;

public class PropertyLifecycleFacadeImpl extends PropertyLifecycle.PropertyLifecycleFacade {

	@Override
	public void after(String key, AfterPropertyExecutor afterPropertyExecutor) {
		StaticPropertyLifecycleMethodsHook.addAfterPropertyExecutor(key, afterPropertyExecutor);
	}

	@Override
	public <T> Store<T> store(String name, Supplier<T> initializer) {
		try {
			return Store.get(name);
		} catch (CannotFindStoreException cannotFindStore) {
			return Store.create(Store.Visibility.LOCAL, name, initializer);
		}
	}
}
