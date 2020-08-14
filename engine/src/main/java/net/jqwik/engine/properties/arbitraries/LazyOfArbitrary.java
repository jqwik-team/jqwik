package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

public class LazyOfArbitrary<T> implements Arbitrary<T> {

	private static final Map<Integer, LazyOfArbitrary<?>> cachedArbitraries = new HashMap<>();

	private final Set<LazyOfShrinkable<T>> generated = new HashSet<>();
	private final Map<Shrinkable<T>, Set<LazyOfShrinkable<T>>> remainingParts = new HashMap<>();

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
			Shrinkable<T> shrinkable = generateCurrent(genSize, index, seed);
			return createShrinkable(shrinkable, genSize, seed, Collections.singleton(index));
		};
	}

	private LazyOfShrinkable<T> createShrinkable(Shrinkable<T> shrinkable, int genSize, long seed, Set<Integer> usedIndices) {
		LazyOfShrinkable<T> lazyOfShrinkable = new LazyOfShrinkable<>(
			shrinkable,
			() -> shrink(shrinkable, genSize, seed, usedIndices)
		);
		generated.add(lazyOfShrinkable);
		return lazyOfShrinkable;
	}

	private Shrinkable<T> generateCurrent(int genSize, int index, long seed) {
		return getArbitrary(index).generator(genSize).next(SourceOfRandomness.newRandom(seed));
	}

	private Stream<Shrinkable<T>> shrink(Shrinkable<T> current, int genSize, long seed, Set<Integer> usedIndexes) {
		return JqwikStreamSupport.concat(
			shrinkToParts(current),
			shrinkCurrent(current, genSize, seed, usedIndexes),
			shrinkToAlternatives(current, genSize, seed, usedIndexes)
		);
	}

	private Stream<Shrinkable<T>> shrinkCurrent(Shrinkable<T> current, int genSize, long seed, Set<Integer> usedIndexes) {
		return current.shrink().map(s -> new LazyOfShrinkable<>(s, () -> shrink(s, genSize, seed, usedIndexes)));
	}

	private Stream<Shrinkable<T>> shrinkToParts(Shrinkable<T> current) {
		ShrinkingDistance distance = current.distance();
		Set<LazyOfShrinkable<T>> partsToConsider = remainingParts.computeIfAbsent(current, ignore -> {
			HashSet<LazyOfShrinkable<T>> parts = new HashSet<>(generated);
			parts.removeIf(s -> s.current.equals(current));
			return parts;
		});
		Stream<LazyOfShrinkable<T>> sorted =
			new HashSet<>(partsToConsider)
				.stream()
				.peek(partsToConsider::remove)
				.filter(shrinkable -> shrinkable.distance().size() <= distance.size())
				.filter(shrinkable -> shrinkable.distance().compareTo(distance) <= 0)
				.sorted();

		List<Shrinkable<T>> list = sorted.collect(Collectors.toList());

		return list.stream();
	}

	private Stream<Shrinkable<T>> shrinkToAlternatives(Shrinkable<T> current, int genSize, long seed, Set<Integer> usedIndexes) {
		ShrinkingDistance distance = current.distance();
		Set<Integer> newUsedIndexes = new HashSet<>(usedIndexes);
		for (int i = 0; i < suppliers.size(); i++) {
			if (usedIndexes.contains(i)) {
				continue;
			}
			Shrinkable<T> next = generateCurrent(genSize, i, seed);
			newUsedIndexes.add(i);
			if (next.equals(current)) {
				continue;
			}
			if (next.distance().size() > distance.size()) {
				continue;
			}
			return Stream.of(createShrinkable(next, genSize, seed, newUsedIndexes));
		}
		return Stream.empty();
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
