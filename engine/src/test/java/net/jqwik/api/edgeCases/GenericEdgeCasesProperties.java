package net.jqwik.api.edgeCases;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public interface GenericEdgeCasesProperties {

	@Provide
	Arbitrary<Arbitrary<?>> arbitraries();

	@Property
	default void askingForZeroEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(0)).hasSize(0);
	}

	@Property
	default void askingForNegativeNumberOfEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(-42)).hasSize(0);
	}

	@Property
	default void mapEachProducesConsistentItemOrder(@ForAll("arbitraries") Arbitrary<?> arbitrary, @ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int size) {
		Arbitrary<Set<Object>> values = arbitrary.<Object>map(x -> new Object() {
			@Override
			public String toString() {
				return JqwikStringSupport.displayString(x);
			}
		}).set().mapEach((set, value) -> value);
		RandomGenerator<Set<Object>> gen = values.generator(1000, true);
		for (int i = 0; i < size; i++) {
			long seed = random.nextLong();
			assertThat(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()))
				.isEqualTo(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()));
		}
	}

	@Property
	default void flatMapEachProducesConsistentItemOrder(@ForAll("arbitraries") Arbitrary<?> arbitrary, @ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int size) {
		Arbitrary<Set<Object>> values = arbitrary.<Object>map(x -> new Object() {
			@Override
			public String toString() {
				return JqwikStringSupport.displayString(x);
			}
		}).set().flatMapEach((set, value) -> Arbitraries.just(value));
		RandomGenerator<Set<Object>> gen = values.generator(1000, true);
		for (int i = 0; i < size; i++) {
			long seed = random.nextLong();
			assertThat(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()))
				.isEqualTo(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()));
		}
	}

	@Property
	default void uniqueElementsProducesConsistentItemOrder(@ForAll("arbitraries") Arbitrary<?> arbitrary, @ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int size) {
		Arbitrary<Set<Object>> values = arbitrary.<Object>map(x -> new Object() {
			@Override
			public String toString() {
				return JqwikStringSupport.displayString(x);
			}
		}).set().uniqueElements(JqwikStringSupport::displayString);
		RandomGenerator<Set<Object>> gen = values.generator(1000, true);
		for (int i = 0; i < size; i++) {
			long seed = random.nextLong();
			assertThat(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()))
				.isEqualTo(JqwikStringSupport.displayString(gen.next(SourceOfRandomness.newRandom(seed)).value()));
		}
	}
}
