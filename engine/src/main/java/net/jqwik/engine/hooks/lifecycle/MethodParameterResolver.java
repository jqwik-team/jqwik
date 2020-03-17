package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;

class MethodParameterResolver {

	static Object[] resolveParameters(Method method, LifecycleContext context) {
		List<Object> parameters = new ArrayList<>();
		for (int i = 0; i < method.getParameters().length; i++) {
			final int index = i;
			ParameterSupplier supplier =
				context
					.resolveParameter(method, index)
					.orElseThrow(() -> {
						String info = "No matching resolver could be found";
						return new CannotResolveParameterException(method.getParameters()[index], info);
					});
			parameters.add(supplier.get(context));
		}
		return parameters.toArray();
	}

}
