package net.jqwik.engine.hooks;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class ResolveReporterHook implements ResolveParameterHook {

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public Optional<ParameterSupplier> resolve(
		ParameterResolutionContext parameterContext,
		LifecycleContext lifecycleContext
	) {
		if (parameterContext.typeUsage().isOfType(Reporter.class)) {
			ParameterSupplier reporterSupplier = ignore -> lifecycleContext.reporter();
			return Optional.of(reporterSupplier);
		}
		return Optional.empty();
	}
}
