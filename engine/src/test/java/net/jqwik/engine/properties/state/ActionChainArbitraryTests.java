package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.assertj.core.api.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.api.state.ActionChain.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@PropertyDefaults(tries = 100)
class ActionChainArbitraryTests {

	@Example
	void deterministicChainCanBeRun(@ForAll Random random) {
		ActionChainArbitrary<String> chains = Chains.actionChains(() -> "", addX()).withMaxTransformations(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);
		assertThat(chain.running()).isEqualTo(RunningState.NOT_RUN);
		assertThat(chain.finalState()).isNotPresent();
		String result = chain.run();

		assertThat(chain.running()).isEqualTo(RunningState.SUCCEEDED);
		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.transformations().size()).isEqualTo(10);
		assertThat(result).isEqualTo("xxxxxxxxxx");
	}

	@Example
	void peekingIntoChain(@ForAll Random random) {
		ActionChainArbitrary<String> chains = Chains.actionChains(() -> "", addX()).withMaxTransformations(5);

		AtomicInteger countPeeks = new AtomicInteger(0);

		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);
		ActionChain<String> peekingChain = chain.peek(state -> {
			assertThat(state).isIn("", "x", "xx", "xxx", "xxxx", "xxxxx", "xxxxxx");
			assertThat(chain.running()).isEqualTo(RunningState.RUNNING);
			countPeeks.incrementAndGet();
		});

		String result = peekingChain.run();
		assertThat(result).isEqualTo("xxxxx");
		assertThat(countPeeks).hasValue(6);
	}

	@Property
	void failingChain(@ForAll("xOrFailing") ActionChain<String> chain) {
		assertThat(chain.running()).isEqualTo(RunningState.NOT_RUN);
		assertThatThrownBy(
			() -> chain.run()
		).isInstanceOf(AssertionFailedError.class);

		assertThat(chain.running()).isEqualTo(RunningState.FAILED);
		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(state -> assertThat(state.chars()).allMatch(c -> c == 'x'));
	}

	@Provide
	ActionChainArbitrary<String> xOrFailing() {
		return Chains.actionChains(() -> "", addX(), failing()).withMaxTransformations(30);
	}

	@Property
	void chainChoosesBetweenTwoActions(@ForAll("xOrY") ActionChain<String> chain) {
		String result = chain.run();

		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.transformations().size()).isGreaterThanOrEqualTo(30);
		assertThat(result).hasSize(chain.transformations().size());
		assertThat(result).contains("x");
		assertThat(result).contains("y");
		assertThat(result.chars()).allMatch(c -> c == 'x' || c == 'y');
	}

	@Provide
	ActionChainArbitrary<String> xOrY() {
		return Chains.actionChains(() -> "", addX(), addY()).withMaxTransformations(30);
	}

	@Property
	void chainUsesRandomizedAction(@ForAll("anyAtoZ") ActionChain<String> chain) {
		String result = chain.run();

		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.transformations().size()).isGreaterThanOrEqualTo(30);
		assertThat(result).hasSize(chain.transformations().size());
		assertThat(result.chars()).allMatch(c -> c >= 'a' && c <= 'z');
	}

	@Provide
	ActionChainArbitrary<String> anyAtoZ() {
		Action<String> anyAZ = new Action<String>() {
			@Override
			public Arbitrary<Transformer<String>> transformer() {
				return Arbitraries.chars().range('a', 'z').map(c -> s -> s + c);
			}
		};
		return Chains.actionChains(() -> "", anyAZ).withMaxTransformations(30);
	}

	@Example
	void preconditionsInSeparateActionsAreConsidered(@ForAll Random random) {
		Action<String> x0to4 = Action.just(
			s -> s.length() < 5,
			s -> s + "x"
		);
		Action<String> y5to9 = Action.just(
			s -> s.length() >= 5,
			s -> s + "y"
		);

		ActionChainArbitrary<String> chains = Chains.actionChains(
			() -> "", x0to4, y5to9
		).withMaxTransformations(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

		String result = chain.run();
		assertThat(result).isEqualTo("xxxxxyyyyy");
	}

	@Example
	void usingEndOfChain(@ForAll Random random) {
		Action<String> x0to4 = Action.just(
			"addX",
			s -> s.length() < 5,
			s -> s + "x"
		);
		Action<String> end = Action.just(
			s -> s.length() >= 5,
			Transformer.endOfChain()
		);

		ActionChainArbitrary<String> chains = Chains.actionChains(
			() -> "", x0to4, end
		).withMaxTransformations(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

		String result = chain.run();
		assertThat(result).isEqualTo("xxxxx");
		assertThat(chain.transformations()).containsExactly(
			"addX",
			"addX",
			"addX",
			"addX",
			"addX",
			Transformer.END_OF_CHAIN.transformation()
		);
	}

	@Property
	void shrinkActionChain(@ForAll Random random) {
		Action<List<Integer>> clear = Action.just(
			"clear",
			(List<Integer> l) -> {
				l.clear();
				return l;
			}
		);
		Action<List<Integer>> add = new Action<List<Integer>>() {
			@Override
			public Arbitrary<Transformer<List<Integer>>> transformer() {
				return Arbitraries.integers()
								  .map(i -> Transformer.mutate(
									  "add " + i,
									  l -> {
										  l.add(i);
										  assertThat(l).hasSizeLessThan(3);
									  }
								  ));
			}
		};


		ActionChainArbitrary<List<Integer>> chains = Chains.actionChains(
			ArrayList::new, clear, add
		).withMaxTransformations(10);

		TestingFalsifier<ActionChain<List<Integer>>> falsifier = c -> {
			c.run();
			return true;
		};
		ActionChain<List<Integer>> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

		assertThat(chain.transformations()).containsExactly(
			"add 0",
			"add 0",
			"add 0"
		);
	}

	@Group
	class StateAccess {

		@Property(tries = 10)
		void actionsAccessStateForCreatingTransformer(@ForAll("numberSets") ActionChain<Set<Integer>> chain) {
			Set<Integer> result = chain.run();

			assertThat(countOdds(result))
				.describedAs("counted odd numbers")
				.isLessThanOrEqualTo(1);
		}

		private long countOdds(Set<Integer> set) {
			return set.stream().filter(n -> n % 2 != 0).count();
		}

		@Provide
		private Arbitrary<ActionChain<Set<Integer>>> numberSets() {
			Action<Set<Integer>> addNumber = new Action<Set<Integer>>() {
				@Override
				public Arbitrary<Transformer<Set<Integer>>> transformer(Set<Integer> state) {
					if (countOdds(state) > 0) {
						return Arbitraries.integers().between(0, 50)
										  .map(i -> i * 2)
										  .map(i -> Transformer.mutate(
											  "add " + i,
											  set -> set.add(i)
										  ));
					} else {
						return Arbitraries.integers().between(0, 100)
										  .map(i -> Transformer.mutate(
											  "add " + i,
											  set -> set.add(i)
										  ));
					}
				}
			};
			return Chains.actionChains(
				HashSet::new,
				Action.just("clear", set -> {
					set.clear();
					return set;
				}),
				addNumber
			).withMaxTransformations(50);
		}

		@Property
		void combiningStateAccessAndPrecondition(@ForAll("shrinkingNumbers") ActionChain<List<Integer>> chain) {
			List<Integer> result = chain.run();

			assertThat(result).allMatch(i -> i >= 0 && i <= 1000);
			for (int i = 1; i < result.size(); i++) {
				int prev = result.get(i - 1);
				assertThat(result.get(i)).isLessThanOrEqualTo(prev);
			}
		}

		@Provide
		private Arbitrary<ActionChain<List<Integer>>> shrinkingNumbers() {
			Action<List<Integer>> addInitialNumber = new Action<List<Integer>>() {
				@Override
				public boolean precondition(List<Integer> state) {
					return state.isEmpty();
				}

				@Override
				public Arbitrary<Transformer<List<Integer>>> transformer() {
					return Arbitraries.integers().between(100, 1000).map(i -> Transformer.mutate(
						"Initial " + i,
						l -> l.add(i)
					));
				}
			};
			Action<List<Integer>> addSmallerNumber = new Action<List<Integer>>() {
				@Override
				public boolean precondition(List<Integer> state) {
					return !state.isEmpty();
				}

				@Override
				public Arbitrary<Transformer<List<Integer>>> transformer(List<Integer> state) {
					int last = state.get(state.size() - 1);
					return Arbitraries.integers().between(0, last).map(i -> Transformer.mutate(
						"Add " + i,
						l -> l.add(i)
					));
				}
			};
			return Chains.actionChains(
				ArrayList::new,
				addInitialNumber,
				addSmallerNumber
			).withMaxTransformations(30);
		}

	}

	@Group
	class Invariants {

		@Property
		boolean succeedingInvariant(@ForAll(supplier = MyModelChain.class) ActionChain<MyModel> chain) {
			ActionChain<MyModel> chainWithInvariant =
				chain.withInvariant(model -> assertThat(true).isTrue());

			MyModel result = chainWithInvariant.run();
			return result.value == null || result.value.length() > 0;
		}

		@Property
		@ExpectFailure(failureType = InvariantFailedError.class)
		void failingInvariant(@ForAll(supplier = MyModelChain.class) ActionChain<MyModel> chain) {
			ActionChain<MyModel> chainWithInvariant =
				chain.withInvariant("never null", model -> assertThat(model.value).isNotNull());
			chainWithInvariant.run();
		}

		class MyModelChain implements ArbitrarySupplier<ActionChain<MyModel>> {
			@Override
			public Arbitrary<ActionChain<MyModel>> get() {
				return Chains.actionChains(MyModel::new, changeValue(), nullify()).withMaxTransformations(20);
			}
		}

		private Action<MyModel> changeValue() {
			return new Action<MyModel>() {
				@Override
				public Arbitrary<Transformer<MyModel>> transformer() {
					return Arbitraries.strings().alpha().ofMinLength(1)
									  .map(aString -> Transformer.transform(
										  "setValue: " + aString,
										  model -> model.setValue(aString)
									  ));
				}
			};
		}

		private Action<MyModel> nullify() {
			return Action.just("nullify", model -> model.setValue(null));
		}

	}

	@Group
	class ConfigurationErrors {

		@Example
		void usingActionWithoutTransformerImplementationFails() {
			Action<String> actionWithoutTransformerImplementation = new Action<String>() {};

			Assertions.assertThatThrownBy(
				() -> Chains.actionChains(() -> "", actionWithoutTransformerImplementation)
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void usingActionWithTwoTransformerImplementationsFails() {
			Action<String> actionWithoutTransformerImplementation = new Action<String>() {
				@Override
				public Arbitrary<Transformer<String>> transformer() {
					return Arbitraries.just(s -> s);
				}

				@Override
				public Arbitrary<Transformer<String>> transformer(String state) {
					return Arbitraries.just(s -> s);
				}
			};

			Assertions.assertThatThrownBy(
				() -> Chains.actionChains(() -> "", actionWithoutTransformerImplementation)
			).isInstanceOf(JqwikException.class);
		}
	}

	private Action<String> addX() {
		return Action.just("+x", model -> model + "x");
	}

	private Action<String> failing() {
		return Action.just(
			"failing", model -> {throw new RuntimeException("failing");}
		);
	}

	private Action<String> addY() {
		return Action.just("+y", model -> model + "y");
	}

	static class MyModel {

		public String value = "";

		MyModel setValue(String aString) {
			this.value = aString;
			return this;
		}

	}

}
