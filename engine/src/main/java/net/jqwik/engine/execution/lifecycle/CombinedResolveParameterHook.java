package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

class CombinedResolveParameterHook implements ResolveParameterHook {

	private List<ResolveParameterHook> resolveParameterHooks;

	public CombinedResolveParameterHook(List<ResolveParameterHook> resolveParameterHooks) {
		this.resolveParameterHooks = resolveParameterHooks;
	}

	@Override
	public Optional<Supplier<Object>> resolve(ParameterResolutionContext parameterContext, LifecycleContext lifecycleContext) {
		List<Tuple.Tuple2<ResolveParameterHook, Optional<Supplier<Object>>>> resolvers =
			resolveParameterHooks.stream()
								 .map(hook -> Tuple.of(hook, hook.resolve(parameterContext, lifecycleContext)))
								 .filter(tuple -> tuple.get2().isPresent())
								 .collect(Collectors.toList());
		if (resolvers.isEmpty()) {
			return Optional.empty();
		}
		if (resolvers.size() > 1) {
			List<Class<? extends ResolveParameterHook>> resolverTypes =
				resolvers.stream()
						 .map(tuple -> tuple.get1().getClass())
						 .collect(Collectors.toList());
			String info = String.format("Competing resolvers %s", JqwikStringSupport.displayString(resolverTypes));
			throw new CannotResolveParameterException(parameterContext, info);
		}
		return resolvers.get(0).get2();
	}
}
