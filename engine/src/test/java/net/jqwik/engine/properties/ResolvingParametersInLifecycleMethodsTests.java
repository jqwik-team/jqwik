package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@AddLifecycleHook(ResolveIntsTo42.class)
class ResolvingParametersInLifecycleMethodsTests {

	@BeforeContainer
	static void beforeContainer(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@AfterContainer
	static void afterContainer(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@BeforeProperty
	void beforeProperty(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@AfterProperty
	void afterProperty(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@BeforeTry
	void beforeTry(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@BeforeTry
	void otherBeforeTry(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@AfterTry
	void afterTry(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	@Property(tries = 5)
	void inProperty(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}
}

class ResolveIntsTo42 implements ResolveParameterHook {

	@Override
	public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext context) {
		assertThat(context).isInstanceOfAny(PropertyLifecycleContext.class, ContainerLifecycleContext.class);
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

