package net.jqwik.engine.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(ResolveIntsTo42.class)
class ResolvingParametersEverywhereTests {

	@BeforeTry
	void beforeTry(int shouldBe42) {
		Assertions.assertThat(shouldBe42).isEqualTo(42);
	}

	@BeforeTry
	void otherBeforeTry(int shouldBe42) {
		Assertions.assertThat(shouldBe42).isEqualTo(42);
	}

	@AfterTry
	void afterTry(int shouldBe42) {
		Assertions.assertThat(shouldBe42).isEqualTo(42);
	}

	@Property(tries = 5)
	void inProperty(int shouldBe42) {
		Assertions.assertThat(shouldBe42).isEqualTo(42);
	}
}

class ResolveIntsTo42 implements ResolveParameterHook {

	@Override
	public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext) {
		if (parameterContext.typeUsage().isOfType(int.class)) {
			return Optional.of(lifecycleContext -> {
				return 42;
			});
		}
		return Optional.empty();
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}
}

