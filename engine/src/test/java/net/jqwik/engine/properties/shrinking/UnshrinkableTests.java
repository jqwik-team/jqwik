package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.SizableArbitrary;
import org.assertj.core.api.Assertions;

import java.util.Random;
import java.util.Set;

class UnshrinkableTests {
	@Example
	void nullValueEquals() {
		Unshrinkable<?> unshrinkable1 = new Unshrinkable<>(null);
		Unshrinkable<?> unshrinkable2 = new Unshrinkable<>(null);
		Assertions.assertThat(unshrinkable1.equals(unshrinkable2)).isTrue();
	}

	@Example
	void nullValueHashCode() {
		Unshrinkable<?> unshrinkable = new Unshrinkable<>(null);
		Assertions.assertThat(unshrinkable.hashCode()).isEqualTo(0);
	}

	@Property(tries = 50)
	void nullValueUnshrinkable(@ForAll Random random) {
		SizableArbitrary<Set<String>> setArbitrary = Arbitraries.strings().injectNull(1.0).set().ofSize(1);	// Element Unshrinkable null value
		Set<?> set = setArbitrary.generator(10).next(random).value();
		Assertions.assertThat(set).isNotEmpty();
		Assertions.assertThat(set.iterator().next()).isNull();
	}
}
