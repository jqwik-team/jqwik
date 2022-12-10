package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

class UnshrinkableTests {

	@Example
	void unshrinkableAreNotBeingShrunk() {
		Shrinkable<String> unshrinkableString = Shrinkable.unshrinkable("a string");
		String shrunkString = shrink(unshrinkableString, ignore -> TryExecutionResult.falsified(null), null);
		assertThat(shrunkString).isEqualTo("a string");
	}

	@Example
	void unshrinkableCanHaveDistance() {
		Shrinkable<String> unshrinkableString = Shrinkable.unshrinkable("a string", ShrinkingDistance.of(42));
		assertThat(unshrinkableString.distance()).isEqualTo(ShrinkingDistance.of(42));
	}

	@Example
	void equals() {
		Shrinkable<?> unshrinkable1 = Shrinkable.unshrinkable("a string");
		Shrinkable<?> unshrinkable2 = Shrinkable.unshrinkable("a string");
		Shrinkable<?> unshrinkable3 = Shrinkable.unshrinkable("different string");
		assertThat(unshrinkable1.equals(unshrinkable2)).isTrue();
		assertThat(unshrinkable1.equals(unshrinkable3)).isFalse();
	}

	@Example
	void hashCodeDifferentForDifferentValues() {
		Shrinkable<?> unshrinkable1 = Shrinkable.unshrinkable("a string");
		Shrinkable<?> unshrinkable2 = Shrinkable.unshrinkable("different string");
		assertThat(unshrinkable1.hashCode()).isNotEqualTo(unshrinkable2.hashCode());
	}

	@Example
	void nullValueEquals() {
		Shrinkable<?> unshrinkable1 = Shrinkable.unshrinkable(null);
		Shrinkable<?> unshrinkable2 = Shrinkable.unshrinkable(null);
		assertThat(unshrinkable1.equals(unshrinkable2)).isTrue();
	}

	@Example
	void nullValueHashCode() {
		Shrinkable<?> unshrinkable = Shrinkable.unshrinkable(null);
		assertThat(unshrinkable.hashCode()).isEqualTo(0);
	}

	@Property(tries = 50)
	void nullValueUnshrinkable(@ForAll JqwikRandom random) {
		SizableArbitrary<Set<String>> setArbitrary =
			Arbitraries.strings().injectNull(1.0).set().ofSize(1);
		Set<?> set = setArbitrary.generator(10, true).next(random).value();
		assertThat(set).isNotEmpty();
		assertThat(set.iterator().next()).isNull();
	}

}
