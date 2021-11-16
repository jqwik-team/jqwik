package net.jqwik.api.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

@Group
class InheritedLifecycleHooksTests {

	@Group
	class GroupWithSupertypes extends SuperclassWithHook implements InterfaceWithHook {

		@Example
		void hookFromClassIsApplied(String aString) {
			Assertions.assertThat(aString).isEqualTo("a string");
		}

		@Example
		void hookFromInterfaceIsApplied(int anInt) {
			Assertions.assertThat(anInt).isEqualTo(42);
		}
	}

	@AddLifecycleHook(StringResolver.class)
	private static class SuperclassWithHook {}

	@AddLifecycleHook(IntResolver.class)
	private interface InterfaceWithHook {}

	private static class StringResolver implements ResolveParameterHook {

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}

		@Override
		public Optional<ParameterSupplier> resolve(
			ParameterResolutionContext parameterContext,
			LifecycleContext lifecycleContext
		) {
			if (parameterContext.typeUsage().isOfType(String.class)) {
				return Optional.of(ignore -> "a string");
			}
			return Optional.empty();
		}
	}

	private static class IntResolver implements ResolveParameterHook {

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}

		@Override
		public Optional<ParameterSupplier> resolve(
			ParameterResolutionContext parameterContext,
			LifecycleContext lifecycleContext
		) {
			if (parameterContext.typeUsage().isAssignableFrom(Integer.class)) {
				return Optional.of(ignore -> 42);
			}
			return Optional.empty();
		}
	}
}
