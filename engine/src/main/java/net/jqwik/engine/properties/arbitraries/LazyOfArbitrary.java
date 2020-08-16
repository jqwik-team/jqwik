package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

public class LazyOfArbitrary<T> implements Arbitrary<T> {

	private static final Map<Integer, LazyOfArbitrary<?>> cachedArbitraries = new HashMap<>();

	private final Deque<Set<LazyOfShrinkable<T>>> generatedParts = new ArrayDeque<>();

	public static <T> Arbitrary<T> of(int hashIdentifier, List<Supplier<Arbitrary<T>>> suppliers) {
		LazyOfArbitrary<?> arbitrary = cachedArbitraries.computeIfAbsent(hashIdentifier, ignore -> new LazyOfArbitrary<>(suppliers));
		if (arbitrary.size() == suppliers.size()) {
			//noinspection unchecked
			return (Arbitrary<T>) arbitrary;
		}
		return new LazyOfArbitrary<>(suppliers);
	}

	private final List<Supplier<Arbitrary<T>>> suppliers;
	private final Arbitrary<T>[] arbitraries;

	public LazyOfArbitrary(List<Supplier<Arbitrary<T>>> suppliers) {
		this.suppliers = suppliers;
		//noinspection unchecked
		this.arbitraries = new Arbitrary[suppliers.size()];
	}

	private int size() {
		return suppliers.size();
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return random -> {
			int index = random.nextInt(suppliers.size());
			long seed = random.nextLong();

			Tuple2<Shrinkable<T>, Set<LazyOfShrinkable<T>>> shrinkableAndParts = generateCurrent(genSize, index, seed);
			return createShrinkable(shrinkableAndParts, genSize, seed, Collections.singleton(index));
		};
	}

	private LazyOfShrinkable<T> createShrinkable(
		Tuple2<Shrinkable<T>, Set<LazyOfShrinkable<T>>> shrinkableAndParts,
		int genSize,
		long seed,
		Set<Integer> usedIndices
	) {
		Shrinkable<T> shrinkable = shrinkableAndParts.get1();
		LazyOfShrinkable<T> lazyOfShrinkable = new LazyOfShrinkable<>(
			shrinkable,
			shrinkableAndParts.get2(),
			(LazyOfShrinkable<T> lazyOf) -> shrink(lazyOf, genSize, seed, usedIndices)
		);
		if (generatedParts.peekFirst() != null) {
			generatedParts.peekFirst().add(lazyOfShrinkable);
		}
		return lazyOfShrinkable;
	}

	private Tuple2<Shrinkable<T>, Set<LazyOfShrinkable<T>>> generateCurrent(int genSize, int index, long seed) {
		try {
			generatedParts.addFirst(new HashSet<>());
			return Tuple.of(
				getArbitrary(index).generator(genSize).next(SourceOfRandomness.newRandom(seed)),
				generatedParts.peekFirst()
			);
		} finally {
			generatedParts.removeFirst();
		}
	}

	private Stream<Shrinkable<T>> shrink(
		LazyOfShrinkable<T> lazyOf,
		int genSize,
		long seed,
		Set<Integer> usedIndexes
	) {
		return JqwikStreamSupport.concat(
			shrinkToParts(lazyOf),
			shrinkCurrent(lazyOf.current, genSize, seed, usedIndexes),
			shrinkToAlternatives(lazyOf.current, genSize, seed, usedIndexes)
		);
	}

	private Stream<Shrinkable<T>> shrinkCurrent(
		Shrinkable<T> current,
		int genSize,
		long seed,
		Set<Integer> usedIndexes
	) {
		return current.shrink().map(shrinkable -> new LazyOfShrinkable<>(
			shrinkable,
			Collections.emptySet(),
			(LazyOfShrinkable<T> lazyOf) -> shrink(lazyOf, genSize, seed, usedIndexes)
		));
	}

	private Stream<Shrinkable<T>> shrinkToParts(LazyOfShrinkable<T> lazyOf) {
		ShrinkingDistance distance = lazyOf.current.distance();
		return lazyOf.parts
				   .stream()
				   .filter(shrinkable -> shrinkable.distance().size() <= distance.size())
				   // .filter(shrinkable -> shrinkable.distance().compareTo(distance) <= 0)
				   .map(s -> s);
	}

	private Stream<Shrinkable<T>> shrinkToAlternatives(Shrinkable<T> current, int genSize, long seed, Set<Integer> usedIndexes) {
		ShrinkingDistance distance = current.distance();
		Set<Integer> newUsedIndexes = new HashSet<>(usedIndexes);
		return IntStream
				   .range(0, suppliers.size())
				   .filter(index -> !usedIndexes.contains(index))
				   .peek(newUsedIndexes::add)
				   .mapToObj(index -> generateCurrent(genSize, index, seed))
				   .filter(shrinkableAndParts -> shrinkableAndParts.get1().distance().compareTo(distance) < 0)
				   .map(shrinkableAndParts -> createShrinkable(shrinkableAndParts, genSize, seed, newUsedIndexes));
	}

	private Arbitrary<T> getArbitrary(int index) {
		if (this.arbitraries[index] == null) {
			this.arbitraries[index] = suppliers.get(index).get();
		}
		return this.arbitraries[index];
	}

	@Override
	public EdgeCases<T> edgeCases() {
		// TODO: Implement edge cases
		return EdgeCases.none();
	}

}
