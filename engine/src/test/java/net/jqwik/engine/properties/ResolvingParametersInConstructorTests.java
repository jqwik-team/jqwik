package net.jqwik.engine.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@Disabled("Not implemented yet")
@AddLifecycleHook(ResolveIntsTo41.class)
class ResolvingParametersInConstructorTests {

	private int shouldBe41;

	public ResolvingParametersInConstructorTests(int shouldBe41) {
		this.shouldBe41 = shouldBe41;
	}

	@Example
	void example() {
		Assertions.assertThat(shouldBe41).isEqualTo(41);
	}

	@Group
	class Inner {
		private int inner41;

		public Inner(int inner41) {
			this.inner41 = inner41;
		}

		@Example
		void innerExample() {
			Assertions.assertThat(shouldBe41).isEqualTo(41);
			Assertions.assertThat(inner41).isEqualTo(41);
		}
	}
}

class ResolveIntsTo41 implements ResolveParameterHook {

	@Override
	public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext) {
		if (parameterContext.typeUsage().isOfType(int.class)) {
			return Optional.of(lifecycleContext -> 42);
		}
		return Optional.empty();
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}
}

