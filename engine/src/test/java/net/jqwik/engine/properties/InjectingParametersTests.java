package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

class InjectingParametersTests {

	@Example
	void nothingToInject() {
		List<MethodParameter> propertyParameters = TestHelper.getParametersFor(TestContainer.class, "nothingToInject");
		Iterator<List<Shrinkable<Object>>> forAllGenerator = shrinkablesIterator(1, 2);
		InjectingParametersGenerator generator = new InjectingParametersGenerator(propertyParameters, forAllGenerator);

		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			Shrinkable.unshrinkable(1)
		);
		assertThat(generator.hasNext()).isTrue();
		assertThat(generator.next()).containsExactly(
			Shrinkable.unshrinkable(2)
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
				Shrinkable<Object> shrinkable = Shrinkable.unshrinkable(valuesIterator.next());
				return Collections.singletonList(shrinkable);
			}
		};
	}

	private static class TestContainer {
		@Property
		void nothingToInject(@ForAll int anInt) {}
	}
}
