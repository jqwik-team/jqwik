package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.*;

public class LazyOfArbitrary<T> implements Arbitrary<T> {

	private static final Map<Integer, LazyOfArbitrary<?>> cachedArbitraries = new HashMap<>();

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
			Shrinkable<T> shrinkable = getArbitrary(index).generator(genSize).next(SourceOfRandomness.newRandom(seed));
			List<Integer> usedIndexes = new ArrayList<>();
			usedIndexes.add(index);
			return new LazyOfShrinkable<>(
				shrinkable, centralShrinker(shrinkable, genSize, seed, usedIndexes)
			);
		};
	}

	Supplier<Stream<Shrinkable<T>>> centralShrinker(Shrinkable<T> toShrink, int genSize, long seed, List<Integer> usedIndexes) {
		return () -> {
			// ShrinkingDistance distance = toShrink.distance();
			// List<Integer> newUsedIndexes = new ArrayList<>(usedIndexes);
			// for (int i = 0; i < suppliers.size(); i++) {
			// 	if (usedIndexes.contains(i)) {
			// 		continue;
			// 	}
			// 	Shrinkable<T> next = getArbitrary(i).generator(genSize).next(SourceOfRandomness.newRandom(seed));
			// 	newUsedIndexes.add(i);
			// 	if (next.equals(toShrink)) {
			// 		continue;
			// 	}
			// 	if (next.distance().size() > distance.size()) {
			// 		continue;
			// 	}
			// 	if (next.distance().compareTo(distance) > 0) {
			// 		continue;
			// 	}
			// 	return Stream.of(
			// 		new LazyOfShrinkable<>(
			// 			next, centralShrinker(next, genSize, seed, newUsedIndexes)
			// 		)
			// 	);
			// }
			return Stream.empty();
		};
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
