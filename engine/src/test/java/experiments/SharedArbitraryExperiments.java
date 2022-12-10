package experiments;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.JqwikRandom;

// Edge cases and (probably) exhaustive generation does not really work with this approach
// Maybe it can be made to work with even more hacking.
// I think, however, that shared values or references or whatever you want to call it must be made
// first-class citizens to that all of shrinking, edge-cases, exhaustive generation etc. works with them.
// The alternative is to translate the set of generated values into a tree of generated values
// with parts of the tree presented as the set of for-all parameters in a property method.
// But this would probably be a major refactoring project for version 2.0 (or 3 or 4 or 5.0).
public class SharedArbitraryExperiments {

	@Property(tries = 100, afterFailure = AfterFailureMode.RANDOM_SEED, edgeCases = EdgeCasesMode.MIXIN)
		// @Report(Reporting.GENERATED)
	void test(@ForAll("ints") int i1, @ForAll("ints") int i2) {
		System.out.println(i1 + " = " + i2);
		// Assertions.assertThat(i1).isLessThan(20);
	}

	@Provide
	Arbitrary<Integer> ints() {
		return new SharedArbitrary<>(Arbitraries.integers(), "first");
	}

}

class SharedArbitrary<T> implements Arbitrary<T> {

	private final Arbitrary<T> base;
	private final Store<Shrinkable<T>> store;

	public SharedArbitrary(Arbitrary<T> base, String key) {
		this.base = base;
		this.store = Store.getOrCreate(
			Tuple.of(base.getClass(), key),
			Lifespan.TRY,
			() -> null
			//,value -> System.out.println("### close: " + value)
		);
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return new SharedGenerator(base.generator(genSize));
	}

	@Override
	public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
		return new SharedGenerator(base.generatorWithEmbeddedEdgeCases(genSize));
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return new SharedEdgeCases(base.edgeCases(maxEdgeCases));
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return base.exhaustive(maxNumberOfSamples);
	}

	class SharedEdgeCases implements EdgeCases<T> {

		private final EdgeCases<T> edgeCases;

		public SharedEdgeCases(EdgeCases<T> edgeCases) {
			this.edgeCases = edgeCases;
		}

		@Override
		public List<Supplier<Shrinkable<T>>> suppliers() {
			return edgeCases.suppliers()
							.stream()
							.map(s -> new SharedSupplier(s))
							.collect(Collectors.toList());
		}
	}

	class SharedSupplier implements Supplier<Shrinkable<T>> {

		private final Supplier<Shrinkable<T>> supplier;

		public SharedSupplier(Supplier<Shrinkable<T>> supplier) {
			this.supplier = supplier;
		}

		@Override
		public Shrinkable<T> get() {
			if (store.get() != null) {
				return new ShrinkableRef();
			}
			Shrinkable<T> shrinkable = supplier.get();
			store.update(ignore -> shrinkable);
			return new SharedShrinkable(shrinkable);
		}
	}

	class SharedGenerator implements RandomGenerator<T> {

		private final RandomGenerator<T> baseGenerator;

		public SharedGenerator(RandomGenerator<T> baseGenerator) {
			this.baseGenerator = baseGenerator;
		}

		@Override
		public Shrinkable<T> next(JqwikRandom random) {
			if (store.get() != null) {
				return new ShrinkableRef();
			}
			Shrinkable<T> shrinkable = baseGenerator.next(random);
			store.update(ignore -> shrinkable);
			return new SharedShrinkable(shrinkable);
		}
	}

	private class SharedShrinkable implements Shrinkable<T> {

		private final Shrinkable<T> shrinkable;

		public SharedShrinkable(Shrinkable<T> shrinkable) {
			this.shrinkable = shrinkable;
		}

		@Override
		public T value() {
			store.update(old -> shrinkable);
			return shrinkable.value();
		}

		@Override
		public Stream<Shrinkable<T>> shrink() {
			return shrinkable.shrink().map(SharedShrinkable::new);
		}

		@Override
		public ShrinkingDistance distance() {
			return shrinkable.distance();
		}
	}

	private class ShrinkableRef implements Shrinkable<T> {

		@Override
		public T value() {
			return store.get().value();
		}

		@Override
		public Stream<Shrinkable<T>> shrink() {
			return Stream.empty();
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(0);
		}
	}
}
