package net.jqwik.engine.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@Disabled("Implementation deferred until restructuring is over")
@AddLifecycleHook(ResolveIntsTo42.class)
class ResolvingParametersEverywhereTests {

	@BeforeTry
	void beforeProperty(int shouldBe42) {
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

