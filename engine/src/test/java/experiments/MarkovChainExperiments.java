package experiments;

import net.jqwik.api.*;

import org.assertj.core.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static net.jqwik.api.Arbitraries.*;

class MarkovChainExperiments {

	@Property(tries = 10)
	void test(@ForAll("chain") List<Integer> integers) {
		int sum = integers.stream().mapToInt(i -> i).sum();
		Assertions.assertThat(sum).isLessThan(100);
	}

	@Provide
	ChainArbitrary<Integer> chain() {
		Function<Integer, Optional<Arbitrary<Integer>>> next = number -> Optional.of(
			integers().between(number, number + 10)
		);

		return Chains.chain(1, next);
	}
}




class Chains {
	static <T> ChainArbitrary<T> chain(T initial, Function<T, Optional<Arbitrary<T>>> next) {
		return new ChainArbitrary<>(initial, next, 10);
	}
}

class ChainArbitrary<T> implements Arbitrary<List<T>> {

	private final T initial;
	private final Function<T, Optional<Arbitrary<T>>> next;
	private final int maxLength;

	public ChainArbitrary(T initial, Function<T, Optional<Arbitrary<T>>> next, int maxLength) {
		this.initial = initial;
		this.next = next;
		this.maxLength = maxLength;
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return random -> {
			List<Shrinkable<T>> shrinkables = new ArrayList<>();
			shrinkables.add(Shrinkable.unshrinkable(initial));

			int count = 1;
			T current = initial;
			while (count < maxLength) {
				Optional<Arbitrary<T>> optionalArbitrary = next.apply(current);
				if (!optionalArbitrary.isPresent()) {
					break;
				}
				Arbitrary<T> arbitrary = optionalArbitrary.get();
				RandomGenerator<T> generator = arbitrary.generator(genSize);
				Shrinkable<T> nextShrinkable = generator.next(random);
				shrinkables.add(nextShrinkable);
				current = nextShrinkable.value();
				count++;
			}

			List<T> values = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
			return Shrinkable.unshrinkable(values);
		};
	}

	@Override
	public EdgeCases<List<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}
}