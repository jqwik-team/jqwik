package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.api.state.Chains.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

class ChainArbitraryTests {

	@Example
	void chainWithSingleGenerator(@ForAll Random random) {
		Function<Supplier<Integer>, Arbitrary<Mutator<Integer>>> growBelow100OtherwiseShrink = intSupplier -> {
			int last = intSupplier.get();
			if (last < 100) {
				return integers().between(0, 10).map(i -> t -> t + i);
			} else {
				return integers().between(1, 10).map(i -> t -> t - i);
			}
		};
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			growBelow100OtherwiseShrink
		).ofMaxSize(50);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		assertThat(chain.maxSize()).isEqualTo(50);
		assertThat(chain.countIterations()).isEqualTo(0);

		Iterator<Integer> iterator = chain.start();
		int last = 1;
		while (iterator.hasNext()) {
			int next = iterator.next();
			if (last < 100) {
				assertThat(next).isGreaterThanOrEqualTo(last);
			} else {
				assertThat(next).isLessThan(last);
			}
			last = next;
		}

		assertThat(chain.countIterations()).isEqualTo(50);
	}

	@Example
	void chainWithSeveralGenerators(@ForAll Random random) {
		Function<Supplier<Integer>, Arbitrary<Mutator<Integer>>> growBelow100otherwiseShrink = intSupplier -> {
			int last = intSupplier.get();
			if (last < 100) {
				return integers().between(0, 10).map(i -> t -> t + i);
			} else {
				return integers().between(1, 10).map(i -> t -> t - i);
			}
		};

		Function<Supplier<Integer>, Arbitrary<Mutator<Integer>>> resetToValueBetween0andLastAbsolute = supplier -> {
			int last = supplier.get();
			return integers().between(0, Math.abs(last)).map(value -> (ignore -> value));
		};

		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			ignore -> Arbitraries.just(i -> i - 1),
			growBelow100otherwiseShrink,
			resetToValueBetween0andLastAbsolute
		).ofMaxSize(50);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		assertThat(chain.maxSize()).isEqualTo(50);
		assertThat(chain.countIterations()).isEqualTo(0);

		Iterator<Integer> iterator = chain.start();
		iterator.forEachRemaining(ignore -> {});

		assertThat(chain.countIterations()).isEqualTo(50);
	}

	@Example
	void chainCanBeRerunWithSameValues(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			ignore -> integers().between(0, 10).map(i -> t -> t + i)
		);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		List<Integer> values1 = collectAllValues(chain);
		List<Integer> values2 = collectAllValues(chain);
		assertThat(values2).isEqualTo(values1);

	}

	@Example
	void generatorThatReturnsNullIsIgnored(@ForAll Random random) {
		Function<Supplier<List<Integer>>, Arbitrary<Mutator<List<Integer>>>> addRandomIntToList =
			ignore -> integers().between(0, 10)
								.map(i -> l -> {
									l.add(i);
									return l;
								});

		Function<Supplier<List<Integer>>, Arbitrary<Mutator<List<Integer>>>> removeFirstElement = supplier -> {
			List<Integer> last = supplier.get();
			if (last.isEmpty()) {
				return null;
			}
			return Arbitraries.just(l -> {
				l.remove(0);
				return l;
			});
		};

		ChainArbitrary<List<Integer>> chains = Chains.chains(
			ArrayList::new,
			addRandomIntToList,
			removeFirstElement
		).ofMaxSize(13);

		Shrinkable<Chain<List<Integer>>> chainShrinkable = chains.generator(100).next(random);

		Chain<List<Integer>> chain = chainShrinkable.value();
		Iterator<List<Integer>> iterator = chain.iterator();
		List<Integer> last = new ArrayList<>();
		while (iterator.hasNext()) {
			int lastSize = last.size();
			List<Integer> next = iterator.next();
			assertThat(lastSize).isNotEqualTo(next.size());
			last = next;
		}
		assertThat(chain.countIterations()).isEqualTo(13);
	}

	@Example
	void stopGenerationIfOnlyNullArbitrariesAreAvailable(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			ignore -> null
		).ofMaxSize(50);

		Chain<Integer> chain = chains.generator(100).next(random).value();

		assertThatThrownBy(() -> {
			chain.start().forEachRemaining(ignore -> {});
		}).isInstanceOf(JqwikException.class);
	}

	private <T> List<T> collectAllValues(Chain<T> chain) {
		List<T> values = new ArrayList<>();
		for (T i : chain) {
			values.add(i);
		}
		return values;
	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinkChainWithoutStateAccessToEnd(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains = Chains.chains(
				() -> 1,
				ignore -> integers().between(0, 10).map(i -> t -> t + i)
			).ofMaxSize(5);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer integer : chain) {
					// consume iterator
				}
				return false;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(collectAllValues(chain)).containsExactly(1);
		}

		@Property
		void removeNullMutatorsDuringShrinking(@ForAll Random random) {
			Mutator<Integer> addOne = Mutator.withName(t -> t + 1, "addOne");
			Mutator<Integer> doNothing = Mutator.withName(t -> t, "doNothing");

			Arbitrary<Chain<Integer>> chains = Chains.chains(
				() -> 1,
				ignore -> Arbitraries.just(addOne),
				ignore -> Arbitraries.just(doNothing)
			).ofMaxSize(20); // Size must be large enough to have at least a single addOne mutator

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				int last = 1;
				for (Integer value : chain) {
					last = value;
				}
				return last <= 1;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(collectAllValues(chain)).containsExactly(2);
		}

		@Property
		void fullyShrinkMutatorsWithoutStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains = Chains.chains(
				() -> 0,
				ignore -> integers().between(1, 5).map(i -> Mutator.withName(t -> t + i, "add" + i))
			).ofMaxSize(10);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				int last = 1;
				for (Integer value : chain) {
					last = value;
				}
				return last < 3;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

			// There are 4 possible "smallest" chains
			assertThat(collectAllValues(chain)).isIn(
				Arrays.asList(3),
				Arrays.asList(1, 3),
				Arrays.asList(2, 3),
				Arrays.asList(1, 2, 3)
			);
		}

		@Property
		void shrinkChainWithStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains = Chains.chains(
				() -> 1,
				supplier -> {
					supplier.get(); // To make shrinkable think the last value is being used
					return integers().between(0, 10).map(i -> t -> t + i);
				}
			).ofMaxSize(10);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				int count = 0;
				int sum = 0;
				for (Integer value : chain) {
					sum += value;
					count++;
					if (count >= 4 && sum >= 4) {
						return false;
					}
				}
				return true;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(collectAllValues(chain)).containsExactly(1, 1, 1, 1);
		}

		@Property
		void shrinkChainWithMixedAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains = Chains.chains(
				() -> 0,
				supplier -> {
					int current = Math.abs(supplier.get());
					if (current > 100) {
						return just(Mutator.withName(t -> t / 2, "half"));
					} else {
						return integers().between(current, current * 2).map(i -> Mutator.withName(t -> t + i, "add-" + i));
					}
				},
				ignore -> Arbitraries.just(Mutator.withName(t -> t - 1, "minus-1"))
			).ofMaxSize(10);

			// Chain<Integer> chain = chains.generator(100).next(random).value();
			// System.out.println(collectAllValues(chain));

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer value : chain) {
					if (value > 20) {
						return false;
					}
				}
				return true;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			List<Integer> series = collectAllValues(chain);
			assertThat(series.get(series.size() - 1))
				.describedAs("Last element of %s", series)
				.isGreaterThan(20);
			assertThat(series.get(series.size() - 2))
				.describedAs("But-last element of %s", series)
				.isLessThanOrEqualTo(20);
		}

	}
}
