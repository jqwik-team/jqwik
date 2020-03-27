package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@AddLifecycleHook(ResolveIntsTo41.class)
class ResolvingParametersInConstructorTests {

	private int shouldBe41;

	public ResolvingParametersInConstructorTests(int shouldBe41) {
		assertThat(ResolveIntsTo41.lastContext.optionalContainerClass().get())
			.isIn(
				ResolvingParametersInConstructorTests.class,
				ResolvingParametersInConstructorTests.Inner.class
			);
		this.shouldBe41 = shouldBe41;
	}

	@Example
	void example() {
		assertThat(shouldBe41).isEqualTo(41);
	}

	@Group
	@AddLifecycleHook(ResolveIntsTo41.class)
	class Inner {
		private int inner41;

		public Inner(int inner41) {
			assertThat(ResolveIntsTo41.lastContext.optionalContainerClass().get())
				.isEqualTo(ResolvingParametersInConstructorTests.Inner.class);
			this.inner41 = inner41;
		}

		@Example
		void innerExample() {
			assertThat(shouldBe41).isEqualTo(41);
			assertThat(inner41).isEqualTo(41);
		}
	}
}

class ResolveIntsTo41 implements ResolveParameterHook {

	static LifecycleContext lastContext;

	@Override
	public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext context) {
		assertThat(context).isInstanceOf(ContainerLifecycleContext.class);
		lastContext = context;
		if (parameterContext.typeUsage().isOfType(int.class)) {
			return Optional.of(optionalTry -> {
				assertThat(optionalTry).isNotPresent();
				return 41;
			});
		}
		return Optional.empty();
	}
}

