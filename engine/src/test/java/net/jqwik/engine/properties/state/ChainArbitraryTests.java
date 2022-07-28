package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@PropertyDefaults(tries = 100)
class ChainArbitraryTests {

	@Example
	void deterministicChain(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 0)
				 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
				 .withMaxTransformations(10);

		Chain<Integer> chain = TestingSupport.generateFirst(chains, random);

		assertThat(collectAllValues(chain)).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		assertThat(chain.transformations()).containsExactly(
			"+1", "+1", "+1", "+1", "+1", "+1", "+1", "+1", "+1", "+1"
		);
	}

	@Example
	void chainWithZeroMaxTransformations(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 0)
				 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
				 .withMaxTransformations(0);

		Chain<Integer> chain = TestingSupport.generateFirst(chains, random);

		assertThat(collectAllValues(chain)).containsExactly(0);
		assertThat(chain.transformations()).isEmpty();
	}

	@Example
	void infiniteChain(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 0)
				 .addTransformation(supplier -> just(Transformer.transform("+1", i -> i + 1)))
				 .infinite();

		Chain<Integer> chain = TestingSupport.generateFirst(chains, random);

		Iterator<Integer> iterator = chain.start();
		int lastValue = -1;
		for (int i = 0; i < 1000; i++) {
			assertThat(iterator).hasNext();
			lastValue = iterator.next();
		}

		assertThat(lastValue).isEqualTo(999);
	}

	@Property
	void chainWithSingleTransformation(@ForAll Random random) {
		Transformation<Integer> growBelow100OtherwiseShrink = intSupplier -> {
			int last = intSupplier.get();
			if (last < 100) {
				return integers().between(0, 10).map(i -> t -> t + i);
			} else {
				return integers().between(1, 10).map(i -> t -> t - i);
			}
		};
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 1)
				 .addTransformation(growBelow100OtherwiseShrink)
				 .withMaxTransformations(50);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		assertThat(chain.maxTransformations()).isEqualTo(50);
		assertThat(chain.transformations()).hasSize(0);

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

		assertThat(chain.transformations()).hasSize(50);
	}

	@Property
	void chainWithSeveralTransformations(@ForAll Random random) {
		Transformation<Integer> growBelow100otherwiseShrink = intSupplier -> {
			int last = intSupplier.get();
			if (last < 100) {
				return integers().between(0, 10).map(i -> t -> t + i);
			} else {
				return integers().between(1, 10).map(i -> t -> t - i);
			}
		};

		Transformation<Integer> resetToValueBetween0andLastAbsolute = supplier -> {
			int last = supplier.get();
			return integers().between(0, Math.abs(last)).map(value -> (ignore -> value));
		};

		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 1)
				 .addTransformation(ignore -> Arbitraries.just(i -> i - 1))
				 .addTransformation(growBelow100otherwiseShrink)
				 .addTransformation(resetToValueBetween0andLastAbsolute)
				 .withMaxTransformations(50);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		assertThat(chain.maxTransformations()).isEqualTo(50);
		assertThat(chain.transformations()).hasSize(0);

		Iterator<Integer> iterator = chain.start();
		iterator.forEachRemaining(ignore -> {});

		assertThat(chain.transformations()).hasSize(50);
	}

	@Property
	void chainCanBeRerunWithSameValues(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 1)
				 .addTransformation(ignore -> integers().between(0, 10).map(i -> t -> t + i));

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		List<Integer> values1 = collectAllValues(chain);
		List<Integer> values2 = collectAllValues(chain);
		assertThat(values2).isEqualTo(values1);
	}

	@Property
	@StatisticsReport(onFailureOnly = true)
	void useFrequenciesToChooseTransformers(@ForAll Random random) {

		Transformation<Integer> just1 = ignore -> Arbitraries.just(t -> 1);
		Transformation<Integer> just2 = ignore -> Arbitraries.just(t -> 2);

		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 0)
				 .addTransformation(1, just1)
				 .addTransformation(4, just2)
				 .withMaxTransformations(10);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);
		Chain<Integer> chain = chainShrinkable.value();

		for (Integer value : chain) {
			Statistics.collect(value);
		}

		Statistics.coverage(checker -> {
			checker.check(0).percentage(p -> p >= 9 && p <= 10); // Always 1 of 11
			checker.check(1).percentage(p -> p > 0 && p < 25);
			checker.check(2).percentage(p -> p > 65);
		});
	}

	@Property
	void transformationPreconditionsAreRespected(@ForAll Random random) {
		Transformation<List<Integer>> addRandomIntToList =
			ignore -> integers().between(0, 10)
								.map(i -> l -> {
									l.add(i);
									return l;
								});

		Transformation<List<Integer>> removeFirstElement =
			Transformation.<List<Integer>>when(last -> !last.isEmpty())
						  .provide(just(l -> {
							  l.remove(0);
							  return l;
						  }));

		ChainArbitrary<List<Integer>> chains =
			Chain.startWith(() -> (List<Integer>) new ArrayList<Integer>())
				 .addTransformation(addRandomIntToList)
				 .addTransformation(removeFirstElement)
				 .withMaxTransformations(13);

		Shrinkable<Chain<List<Integer>>> chainShrinkable = chains.generator(100).next(random);

		Chain<List<Integer>> chain = chainShrinkable.value();
		Iterator<List<Integer>> iterator = chain.iterator();
		iterator.next(); // Ignore initial state

		List<Integer> last = new ArrayList<>();
		while (iterator.hasNext()) {
			int lastSize = last.size();
			List<Integer> next = iterator.next();
			assertThat(lastSize).isNotEqualTo(next.size());
			last = next;
		}
		assertThat(chain.transformations()).hasSize(13);
	}

	@Property
	void noopTransformersAreIgnored(@ForAll Random random) {
		Transformation<Integer> addOne =
			ignore -> just(1).map(toAdd -> i -> i + toAdd);

		Transformation<Integer> justNoop = ignore -> just(Transformer.noop());
		Transformation<Integer> noopOrNoop = ignore -> of(Transformer.noop(), Transformer.noop());

		ChainArbitrary<Integer> chains =
			Chain.startWith(() -> 0)
				 .addTransformation(addOne)
				 .addTransformation(justNoop)
				 .addTransformation(noopOrNoop)
				 .withMaxTransformations(13);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();
		Iterator<Integer> iterator = chain.iterator();

		int last = iterator.next();
		while (iterator.hasNext()) {
			int next = iterator.next();
			assertThat(last + 1).isEqualTo(next);
			last = next;
		}
		assertThat(chain.transformations()).hasSize(13);
	}

	@Example
	void stopGenerationIfNoTransformerApplies(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 1)
				 .addTransformation(
					 Transformation.<Integer>when(ignore -> false)
								   .provide((Arbitrary<Transformer<Integer>>) null) // never gets here
				 )
				 .addTransformation(ignore -> just(Transformer.noop())) // noop() is ignored
				 .withMaxTransformations(50);

		Chain<Integer> chain = chains.generator(100).next(random).value();

		assertThatThrownBy(() -> {
			chain.start().forEachRemaining(ignore -> {});
		}).isInstanceOf(JqwikException.class);
	}

	@Example
	void failToCreateGeneratorIfNoTransformersAreProvided(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chain.startWith(() -> 1).withMaxTransformations(50);

		assertThatThrownBy(() -> {
			chains.generator(100).next(random).value();
		}).isInstanceOf(JqwikException.class);
	}

	@Property(tries = 5)
	void concurrentlyIteratingChainProducesSameResult(@ForAll Random random) throws Exception {
		Arbitrary<Chain<Integer>> chains =
			Chain.startWith(() -> 1)
				 .addTransformation(ignore -> Arbitraries.integers().between(1, 10).map(i -> t -> t + i))
				 .withMaxTransformations(30);

		Chain<Integer> chain = chains.generator(100).next(random).value();

		Callable<List<Integer>> allValuesCallable = () -> {
			List<Integer> values = new ArrayList<>();
			for (Integer value : chain) {
				Thread.sleep(random.nextInt(10));
				values.add(value);
			}
			return values;
		};

		ExecutorService service = Executors.newFixedThreadPool(3);
		List<Future<List<Integer>>> futures = service.invokeAll(
			Arrays.asList(allValuesCallable, allValuesCallable, allValuesCallable)
		);

		List<Integer> values1 = futures.get(0).get();
		List<Integer> values2 = futures.get(1).get();
		List<Integer> values3 = futures.get(2).get();

		assertThat(values1).hasSize(30 + 1);
		assertThat(values1).isEqualTo(values2);
		assertThat(values1).isEqualTo(values3);
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
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(ignore -> integers().between(0, 10).map(i -> t -> t + i))
					 .withMaxTransformations(5);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer integer : chain) {
					// consume iterator
				}
				return false;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(chain.maxTransformations());
			assertThat(collectAllValues(chain)).containsExactly(0);
		}

		@Property
		void shrinkChainWithStateAccessToEnd(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(
						 supplier -> {
							 supplier.get(); // To signal state access
							 return integers().between(0, 10).map(i -> t -> t + i);
						 }
					 ).withMaxTransformations(5);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer integer : chain) {
					// consume iterator
				}
				return false;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(chain.maxTransformations());
			assertThat(collectAllValues(chain)).containsExactly(0);
		}

		@Property
		void removeTransformersThatDontChangeStateDuringShrinking(@ForAll Random random) {
			Transformer<Integer> addOne = Transformer.transform("addOne", t1 -> t1 + 1);
			Transformer<Integer> doNothing = Transformer.transform("doNothing", t -> t);

			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 1)
					 .addTransformation(ignore -> Arbitraries.just(addOne))
					 .addTransformation(ignore -> Arbitraries.just(doNothing))
					 .withMaxTransformations(20); // Size must be large enough to have at least a single addOne transformer

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				int last = 1;
				for (Integer value : chain) {
					last = value;
				}
				return last <= 1;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(chain.maxTransformations());
			assertThat(collectAllValues(chain)).containsExactly(1, 2);
		}

		@Property
		void fullyShrinkTransformersWithoutStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(ignore -> integers().between(1, 5).map(i -> Transformer.transform("add" + i, t -> t + i)))
					 .withMaxTransformations(10);

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
				Arrays.asList(0, 3),
				Arrays.asList(0, 1, 3),
				Arrays.asList(0, 2, 3),
				Arrays.asList(0, 1, 2, 3)
			);
		}

		@Property
		void shrinkChainWithStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 1)
					 .addTransformation(
						 supplier -> {
							 supplier.get(); // To make shrinkable think the last value is being used
							 return integers().between(0, 10).map(i -> t -> t + i);
						 }
					 ).withMaxTransformations(10);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				int count = 0;
				int sum = 0;
				for (Integer value : chain) {
					sum += value;
					count++;
					if (count >= 5 && sum >= 5) {
						return false;
					}
				}
				return true;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(chain.maxTransformations());
			assertThat(collectAllValues(chain)).containsExactly(1, 1, 1, 1, 1);
		}

		@Property
		void preconditionedEndOfChainCanBeShrunkAwayInFiniteChain(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(Transformation.<Integer>when(i -> i >= 5).provide(just(Transformer.endOfChain())))
					 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
					 .withMaxTransformations(100);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> collectAllValues(chain).size() < 6;
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(5);
			assertThat(chain.transformations()).doesNotContain(Transformer.END_OF_CHAIN.transformation());
			assertThat(collectAllValues(chain)).containsExactly(0, 1, 2, 3, 4, 5);
		}

		@Property
		void endOfChainCanBeShrunkAwayInFiniteChain(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
					 .addTransformation(ignore -> just(Transformer.endOfChain()))
					 .withMaxTransformations(100);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer value : chain) {
					// consume iterator
				}
				return false;
			};
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).isEmpty();
			assertThat(collectAllValues(chain)).containsExactly(0);
		}

		@Property
		void shrinkInfiniteChainWithPrecondition(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(Transformation.<Integer>when(i -> i >= 5).provide(just(Transformer.endOfChain())))
					 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
					 .infinite();

			TestingFalsifier<Chain<Integer>> falsifier = chain -> collectAllValues(chain).size() < 6;
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(6);
			assertThat(chain.transformations()).endsWith(Transformer.END_OF_CHAIN.transformation());
			assertThat(collectAllValues(chain)).containsExactly(0, 1, 2, 3, 4, 5);
		}

		@Property
		void shrinkInfiniteChainWithoutStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(ignore -> just(Transformer.transform("+1", i -> i + 1)))
					 .addTransformation(ignore -> just(Transformer.endOfChain()))
					 .infinite();

			TestingFalsifier<Chain<Integer>> falsifier = chain -> collectAllValues(chain).size() < 6;
			Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);
			assertThat(chain.transformations()).hasSize(6);
			assertThat(chain.transformations()).endsWith(Transformer.END_OF_CHAIN.transformation());
			assertThat(collectAllValues(chain)).containsExactly(0, 1, 2, 3, 4, 5);
		}

		@Property
		void shrinkChainWithMixedAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(
						 supplier -> {
							 int current = Math.abs(supplier.get());
							 if (current > 100) {
								 return just(Transformer.transform("half", t -> t / 2));
							 } else {
								 return integers().between(current, current * 2).map(i -> Transformer.transform("add-" + i, t -> t + i));
							 }
						 })
					 .addTransformation(ignore -> Arbitraries.just(Transformer.transform("minus-1", t -> t - 1)))
					 .withMaxTransformations(10);

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
			assertThat(chain.transformations()).hasSize(chain.maxTransformations());
			assertThat(series.get(series.size() - 1))
				.describedAs("Last element of %s", series)
				.isBetween(21, 40); // It's either 21 or the double of the but-last value
			assertThat(series.get(series.size() - 2))
				.describedAs("But-last element of %s", series)
				.isLessThanOrEqualTo(20);
		}

		@Property
		void whenShrinkingTryToRemoveTransformersWithStateAccess(@ForAll Random random) {
			Arbitrary<Chain<Integer>> chains =
				Chain.startWith(() -> 0)
					 .addTransformation(supplier -> {
						 int current = supplier.get(); // access state
						 return Arbitraries.just(Transformer.transform("plus-1", ignore -> current + 1));
					 })
					 .addTransformation(ignore -> Arbitraries.just(Transformer.transform("plus-2", t -> t + 2)))
					 .withMaxTransformations(20);

			RandomGenerator<Chain<Integer>> generator = chains.generator(100, false);
			Shrinkable<Chain<Integer>> shrinkable = TestingSupport.generateUntil(
				generator,
				random,
				this::hasAtLeastFivePlus2Transformers
			);

			TestingFalsifier<Chain<Integer>> falsifier = chain -> {
				for (Integer value : chain) {
					if (value > 9) {
						return false;
					}
				}
				return true;
			};
			Chain<Integer> shrunkChain = ShrinkingSupport.shrink(shrinkable, falsifier, null);

			List<Integer> series = collectAllValues(shrunkChain);
			assertThat(series).containsExactly(0, 2, 4, 6, 8, 10);
			assertThat(shrunkChain.maxTransformations()).isEqualTo(5);
		}

		@Property
		void shrinkPairsOfIterations(@ForAll Random random) {
			ChainArbitrary<List<Integer>> chains =
				Chain.startWith(() -> (List<Integer>) new ArrayList<Integer>())
					 .addTransformation(ignore -> integers().map(i -> Transformer.mutate("add " + i, l -> l.add(i))))
					 .addTransformation(
						 Transformation.<List<Integer>>when(list -> !list.isEmpty())
									   .provide(
										   list -> Arbitraries.of(list)
															  .map(i -> Transformer.mutate("duplicate " + i, l -> l.add(i)))
									   ))
					 .withMaxTransformations(20);

			TestingFalsifier<Chain<List<Integer>>> falsifier = chain -> {
				for (List<Integer> list : chain) {
					// Fail on duplicates
					if (new LinkedHashSet<>(list).size() < list.size()) {
						return false;
					}
				}
				return true;
			};
			Chain<List<Integer>> shrunkChain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

			for (List<Integer> value : shrunkChain) {
				// evaluate chain
			}
			assertThat(shrunkChain.transformations()).isIn(
				Arrays.asList("add 0", "add 0"),
				Arrays.asList("add 0", "duplicate 0")
			);
		}

		@Property
		void whenUsingChangeDetector_shrinkAwayPartsThatDontChangeState(@ForAll Random random) {
			ChainArbitrary<String> chains =
				Chain.startWith(() -> "")
					 .addTransformation(ignore -> chars().alpha().map(c -> Transformer.transform("append " + c, s -> s + c)))
					 .addTransformation(ignore -> just(Transformer.transform("nothing", s -> s)))
					 .addTransformation(ignore -> just(Transformer.noop()))
					 .addTransformation(Transformation.<String>when(string -> !string.isEmpty())
													  .provide(
														  value -> Arbitraries.of(value.toCharArray())
																			  .map(c -> Transformer.transform("duplicate " + c, s -> s + c))
													  ))
					 .withMaxTransformations(20)
					 .improveShrinkingWith(ChangeDetector::forImmutables);

			TestingFalsifier<Chain<String>> falsifier = chain -> {
				for (String value : chain) {
					// Fail on duplicate chars
					long uniqueChars = value.chars().boxed().distinct().count();
					if (uniqueChars < value.length()) {
						return false;
					}
				}
				return true;
			};
			Chain<String> shrunkChain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

			for (String value : shrunkChain) {
				// evaluate chain
			}
			assertThat(shrunkChain.transformations()).isIn(
				Arrays.asList("append a", "append a"),
				Arrays.asList("append A", "append A"),
				Arrays.asList("append A", "duplicate A")
			);
		}

		private boolean hasAtLeastFivePlus2Transformers(Chain<Integer> c) {
			for (Integer value : c) {} // consume chain
			long countPlusTwoBefore = c.transformations().stream().filter(s -> s.equals("plus-2")).count();
			return countPlusTwoBefore >= 5;
		}

	}
}
