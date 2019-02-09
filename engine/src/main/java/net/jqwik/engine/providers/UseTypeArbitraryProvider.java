package net.jqwik.engine.providers;

import java.util.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class UseTypeArbitraryProvider implements ArbitraryProvider {

	private static final Logger LOG = Logger.getLogger(UseTypeArbitraryProvider.class.getName());

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isAnnotated(UseType.class)) {
			return false;
		}
		if (!targetType.getTypeArguments().isEmpty()) {
			LOG.warning("@UseType cannot be applied to parameterized types");
			return false;
		}
		return true;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(createForTypeArbitrary(targetType));
	}

	private TypeArbitrary<?> createForTypeArbitrary(TypeUsage targetType) {
		TypeArbitrary<?> typeArbitrary = Arbitraries.forType(targetType.getRawType());

		UseTypeMode[] uses = targetType.findAnnotation(UseType.class)
									   .map(UseType::value)
									   .orElse(new UseTypeMode[0]);

		for (UseTypeMode use : uses) {
			typeArbitrary = use.modify(typeArbitrary);
		}

		return typeArbitrary;
	}

	@Override
	public int priority() {
		// @UseType should override other default providers with priority 0
		return 1;
	}
}
