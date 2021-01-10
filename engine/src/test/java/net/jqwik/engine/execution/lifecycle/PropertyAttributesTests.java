package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class PropertyAttributesTests {

	@AddLifecycleHook(ChangeTriesAndSeedTo42.class)
	@Property(tries = 10, seed = "10")
	void attributesAreChangedByHook(@ForAll int i) {}

	private static class ChangeTriesAndSeedTo42 implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			PropertyAttributes attributes = context.attributes();
			assertThat(attributes.tries()).isEqualTo(Optional.of(10));
			assertThat(attributes.seed()).isEqualTo(Optional.of("10"));
			assertThat(attributes.edgeCases()).isEmpty();
			attributes.setTries(42);
			attributes.setSeed("42");

			PropertyExecutionResult result = property.execute();

			assertThat(result.countTries()).isEqualTo(42);
			assertThat(result.seed()).isEqualTo(Optional.of("42"));
			return result;
		}
	}
}
