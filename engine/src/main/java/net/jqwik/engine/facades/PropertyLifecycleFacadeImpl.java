package net.jqwik.engine.facades;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PropertyLifecycle.*;
import net.jqwik.engine.hooks.lifecycle.*;

public class PropertyLifecycleFacadeImpl extends PropertyLifecycle.PropertyLifecycleFacade {

	@Override
	public void after(String key, AfterPropertyExecutor afterPropertyExecutor) {
		StaticPropertyLifecycleMethodsHook.addAfterPropertyExecutor(key, afterPropertyExecutor);
	}
}
