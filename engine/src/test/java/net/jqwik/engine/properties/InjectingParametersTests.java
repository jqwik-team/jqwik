package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Shrinkable.*;

class InjectingParametersTests {

	@Example
	void nothingToInject() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "nothingToInject");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(asList(1), asList(2));
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
	void injectLastPosition() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "forAllIntAndString");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(asList(1), asList(2));
		InjectParameterHook stringInjector = parameterContext -> {
			if (parameterContext.usage().isOfType(String.class)) {
				return Optional.of("aString");
			}
			return Optional.empty();
		};
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

	@Example
	void injectSeveralPositions() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "stringIntStringInt");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(asList(1, 2), asList(3, 4));
		InjectParameterHook stringInjector = parameterContext -> {
			if (parameterContext.usage().isOfType(String.class)) {
				return Optional.of("aString");
			}
			return Optional.empty();
		};
		InjectingParametersGenerator generator = new InjectingParametersGenerator(
			propertyParameters,
			forAllGenerator,
			stringInjector
		);

		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable("aString"),
			unshrinkable(1),
			unshrinkable("aString"),
			unshrinkable(2)
		);
		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			unshrinkable("aString"),
			unshrinkable(3),
			unshrinkable("aString"),
			unshrinkable(4)
		);
		assertThat(generator.hasNext()).isFalse();
	}

	@SafeVarargs
	private final Iterator<List<Shrinkable<Object>>> shrinkablesIterator(List<Object>... lists) {
		Iterator<List<Object>> valuesIterator = Arrays.stream(lists).iterator();

		return new Iterator<List<Shrinkable<Object>>>() {
			@Override
			public boolean hasNext() {
				return valuesIterator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next() {
				List<Object> values = valuesIterator.next();
				return values.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList());
			}
		};
	}

	private static class TestContainer {
		@Property
		void nothingToInject(@ForAll int anInt) {}

		@Property
		void forAllIntAndString(@ForAll int anInt, String aString) {}

		@Property
		void stringIntStringInt(String s1, @ForAll int i1, String s2, @ForAll int i2) {}
	}
}
