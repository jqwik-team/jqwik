package net.jqwik.engine.facades;

import net.jqwik.api.facades.*;
import net.jqwik.engine.support.*;

public class ReflectionSupportFacadeImpl extends ReflectionSupportFacade {

	@Override
	public <T> T newInstanceInTestContext(Class<T> clazz, Object context) {
		return JqwikReflectionSupport.newInstanceInTestContext(clazz, context);
	}
}
