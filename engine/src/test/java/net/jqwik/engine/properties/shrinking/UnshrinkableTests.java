package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

class UnshrinkableTests {

	@Example
	void unshrinkableAreNotBeingShrunk() {
		Shrinkable<String> unshrinkableString = new Unshrinkable<>("a string");

		ShrinkingSequence<String> shrinkingSequence = unshrinkableString.shrink(ignore -> false);
		while(shrinkingSequence.next(() -> {}, ignore -> {})) {}

		assertThat(shrinkingSequence.current().value()).isEqualTo("a string");
	}

	@Example
	void equals() {
		Unshrinkable<?> unshrinkable1 = new Unshrinkable<>("a string");
		Unshrinkable<?> unshrinkable2 = new Unshrinkable<>("a string");
		Unshrinkable<?> unshrinkable3 = new Unshrinkable<>("different string");
		assertThat(unshrinkable1.equals(unshrinkable2)).isTrue();
		assertThat(unshrinkable1.equals(unshrinkable3)).isFalse();
	}

	@Example
	void hashCodeDifferentForDifferentValues() {
		Unshrinkable<?> unshrinkable1 = new Unshrinkable<>("a string");
		Unshrinkable<?> unshrinkable2 = new Unshrinkable<>("different string");
		assertThat(unshrinkable1.hashCode()).isNotEqualTo(unshrinkable2.hashCode());
	}

	@Example
	void nullValueEquals() {
		Unshrinkable<?> unshrinkable1 = new Unshrinkable<>(null);
		Unshrinkable<?> unshrinkable2 = new Unshrinkable<>(null);
		assertThat(unshrinkable1.equals(unshrinkable2)).isTrue();
	}

	@Example
	void nullValueHashCode() {
		Unshrinkable<?> unshrinkable = new Unshrinkable<>(null);
		assertThat(unshrinkable.hashCode()).isEqualTo(0);
	}

	@Property(tries = 50)
	void nullValueUnshrinkable(@ForAll Random random) {
		SizableArbitrary<Set<String>> setArbitrary =
			Arbitraries.strings().injectNull(1.0).set().ofSize(1);
		Set<?> set = setArbitrary.generator(10).next(random).value();
		assertThat(set).isNotEmpty();
		assertThat(set.iterator().next()).isNull();
	}

}
