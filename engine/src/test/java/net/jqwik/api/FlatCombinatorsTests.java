package net.jqwik.api;

import java.util.*;

import net.jqwik.engine.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

class FlatCombinatorsTests {

	private final JqwikRandom random = SourceOfRandomness.current();

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two())
												 .flatAs((a, b) -> Arbitraries.just(a + b));
		Shrinkable<Integer> value = generate(combine2);
		assertThat(value.value()).isEqualTo(3);
	}

	@Example
	void filteringWorksWithFlatAs(@ForAll JqwikRandom random) {
		Arbitrary<Integer> upToThree = Arbitraries.integers().between(0, 3);
		Arbitrary<Integer> combine2 = Combinators.combine(upToThree, upToThree)
												 .filter((a, b) -> a + b == 3)
												 .flatAs((a, b) -> Arbitraries.just(a + b));
		assertAllGenerated(combine2, random, value -> {
			assertThat(value).isEqualTo(3);
		});
	}

	private Shrinkable<Integer> generate(Arbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1).next(random);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine3 = Combinators.combine(one(), two(), three())
												 .flatAs((a, b, c) -> Arbitraries.just(a + b + c));
		Shrinkable<Integer> value = generate(combine3);
		assertThat(value.value()).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine4 = Combinators.combine(one(), two(), three(), four())
												 .flatAs((a, b, c, d) -> Arbitraries.just(a + b + c + d));
		Shrinkable<Integer> value = generate(combine4);
		assertThat(value.value()).isEqualTo(10);
	}

	@Example
	void fiveArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine5 = Combinators.combine(one(), two(), three(), four(), five())
												 .flatAs((a, b, c, d, e) -> Arbitraries.just(a + b + c + d + e));
		Shrinkable<Integer> value = generate(combine5);
		assertThat(value.value()).isEqualTo(15);
	}

	@Example
	void sixArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine6 = Combinators.combine(one(), two(), three(), four(), five(), six())
												 .flatAs((a, b, c, d, e, f) -> Arbitraries.just(a + b + c + d + e + f));
		Shrinkable<Integer> value = generate(combine6);
		assertThat(value.value()).isEqualTo(21);
	}

	@Example
	void sevenArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine7 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven())
												 .flatAs((a, b, c, d, e, f, g) -> Arbitraries.just(a + b + c + d + e + f + g));
		Shrinkable<Integer> value = generate(combine7);
		assertThat(value.value()).isEqualTo(28);
	}

	@Example
	void eightArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine8 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven(), eight())
												 .flatAs((a, b, c, d, e, f, g, h) -> Arbitraries.just(a + b + c + d + e + f + g + h));
		RandomGenerator<Integer> generator = combine8.generator(1000);
		TestingSupport.assertAllGeneratedEqualTo(generator, random, 36);
	}

	@Example
	void listOfArbitrariesCanBeCombined(@ForAll JqwikRandom random) {
		List<Arbitrary<Integer>> listOfArbitraries = Arrays.asList(one(), two(), three());
		Arbitrary<Integer> combineList =
			Combinators.combine(listOfArbitraries)
					   .flatAs(list -> {
						   assertThat(list).hasSize(3);
						   assertThat(list.get(0)).isEqualTo(1);
						   assertThat(list.get(1)).isEqualTo(2);
						   assertThat(list.get(2)).isEqualTo(3);
						   return Arbitraries.just(list.stream().mapToInt(e -> e).sum());
					   });
		RandomGenerator<Integer> generator = combineList.generator(1000);
		TestingSupport.assertAllGeneratedEqualTo(generator, random, 6);
	}

	Arbitrary<Integer> one() {
		return Arbitraries.of(1);
	}

	Arbitrary<Integer> two() {
		return Arbitraries.of(2);
	}

	Arbitrary<Integer> three() {
		return Arbitraries.of(3);
	}

	Arbitrary<Integer> four() {
		return Arbitraries.of(4);
	}

	Arbitrary<Integer> five() {
		return Arbitraries.of(5);
	}

	Arbitrary<Integer> six() {
		return Arbitraries.of(6);
	}

	Arbitrary<Integer> seven() {
		return Arbitraries.of(7);
	}

	Arbitrary<Integer> eight() {
		return Arbitraries.of(8);
	}
}
