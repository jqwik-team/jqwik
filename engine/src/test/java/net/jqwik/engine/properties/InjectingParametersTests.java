package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Shrinkable.*;

class InjectingParametersTests {

	@Example
	void nothingToInject() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "nothingToInject");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(1, 2);
		InjectingParametersGenerator generator = new InjectingParametersGenerator(
			propertyParameters,
			forAllGenerator,
			InjectParameterHook.INJECT_NOTHING
		);

		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable(1)
		);
		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable(2)
		);
		assertThat(generator.hasNext()).isFalse();
	}

	@Example
	void injectString() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "forAllIntAndString");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(1, 2);
		InjectParameterHook stringInjector = ((parameterContext, tryLifecycleContext) -> {
			if (parameterContext.usage().isOfType(String.class)) {
				return Optional.of("aString");
			}
			return Optional.empty();
		});
		InjectingParametersGenerator generator = new InjectingParametersGenerator(
			propertyParameters,
			forAllGenerator,
			stringInjector
		);

		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable(1),
			unshrinkable("aString")
		);
		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable(2),
			unshrinkable("aString")
		);
		assertThat(generator.hasNext()).isFalse();
	}

	private Iterator<List<Shrinkable<Object>>> shrinkablesIterator(int... values) {
		Iterator<Integer> valuesIterator = Arrays.stream(values).iterator();

		return new Iterator<List<Shrinkable<Object>>>() {
			@Override
			public boolean hasNext() {
				return valuesIterator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next() {
				Shrinkable<Object> shrinkable = unshrinkable(valuesIterator.next());
				return Collections.singletonList(shrinkable);
			}
		};
	}

	private static class TestContainer {
		@Property
		void nothingToInject(@ForAll int anInt) {}

		@Property
		void forAllIntAndString(@ForAll int anInt, String aString) {}
	}
}
