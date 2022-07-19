package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

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
		ActionChainArbitrary<String> chains = ActionChain.actionChains(() -> "", addX()).withMaxTransformations(10);
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
		ActionChainArbitrary<String> chains = ActionChain.actionChains(() -> "", addX()).withMaxTransformations(5);

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
		return ActionChain.actionChains(() -> "", addX(), failing()).withMaxTransformations(30);
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
		return ActionChain.actionChains(() -> "", addX(), addY()).withMaxTransformations(30);
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
		Action.Independent<String> anyAZ = () -> Arbitraries.chars().range('a', 'z').map(c -> s -> s + c);
		return ActionChain.actionChains(() -> "", anyAZ).withMaxTransformations(30);
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

		ActionChainArbitrary<String> chains = ActionChain.actionChains(
			() -> "", x0to4, y5to9
		).withMaxTransformations(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

		String result = chain.run();
		assertThat(result).isEqualTo("xxxxxyyyyy");
	}

	@Example
	void usingEndOfChain(@ForAll Random random) {
		Action.Independent<String> x0to4 = Action.just(
			"addX",
			s -> s.length() < 5,
			s -> s + "x"
		);
		Action.Independent<String> end = Action.just(
			s -> s.length() >= 5,
			Transformer.endOfChain()
		);

		ActionChainArbitrary<String> chains = ActionChain.actionChains(
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

	@Group
	class Shrinking {

		@Property
		void shrinkActionChain(@ForAll Random random) {
			Action.Independent<List<Integer>> clear = Action.just(
				"clear",
				(List<Integer> l) -> {
					l.clear();
					return l;
				}
			);
			Action.Independent<List<Integer>> add =
				() -> Arbitraries.integers()
								 .map(i -> Transformer.mutate(
									 "add " + i,
									 l -> {
										 l.add(i);
										 assertThat(l).hasSizeLessThan(3);
									 }
								 ));

			ActionChainArbitrary<List<Integer>> chains = ActionChain.actionChains(
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

		@Property
		void shrinkWithChangeDetector(@ForAll Random random) {
			Action.Independent<List<Integer>> nothing = Action.just(
				"nothing", l -> l
			);
			Action.Dependent<List<Integer>> add =
				state -> Arbitraries.integers().greaterOrEqual(0)
									.filter(i -> !state.contains(i))
									.map(i -> Transformer.mutate(
										"add " + i,
										l -> l.add(i)
									));

			Supplier<ChangeDetector<List<Integer>>> changeOfListDetector = () -> new ChangeDetector<List<Integer>>() {
				private List<Integer> before;

				@Override
				public void before(List<Integer> before) {
					this.before = new ArrayList<>(before);
				}

				@Override
				public boolean hasChanged(List<Integer> after) {
					return !Objects.equals(before, after);
				}
			};

			ActionChainArbitrary<List<Integer>> chains = ActionChain.actionChains(
				ArrayList::new, nothing, add
			).withMaxTransformations(10).detectChangesWith(changeOfListDetector);

			TestingFalsifier<ActionChain<List<Integer>>> falsifier = chain -> {
				chain.withInvariant(l -> assertThat(l).hasSizeLessThan(2)).run();
				return true;
			};
			ActionChain<List<Integer>> shrunkChain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

			List<String> transformations = shrunkChain.transformations();
			assertThat(transformations).isIn(
				Arrays.asList("add 0", "add 1"),
				Arrays.asList("add 1", "add 0")
			);
		}
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
			Action.Dependent<Set<Integer>> addNumber = state -> {
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
			};
			return ActionChain.actionChains(
				LinkedHashSet::new,
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
			Action.Independent<List<Integer>> addInitialNumber = new Action.Independent<List<Integer>>() {
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
			Action.Dependent<List<Integer>> addSmallerNumber = new Action.Dependent<List<Integer>>() {
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
			return ActionChain.actionChains(
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

		@Property
		@ExpectFailure(failureType = InvariantFailedError.class)
		void failInvariantOnInitialState(@ForAll(supplier = MyModelChain.class) ActionChain<MyModel> chain) {
			ActionChain<MyModel> chainWithInvariant =
				chain.withInvariant("never null", model -> assertThat(model.value).isEqualTo("oops"));
			chainWithInvariant.run();
		}

		class MyModelChain implements ArbitrarySupplier<ActionChain<MyModel>> {
			@Override
			public Arbitrary<ActionChain<MyModel>> get() {
				return ActionChain.actionChains(MyModel::new, changeValue(), nullify()).withMaxTransformations(20);
			}
		}

		private Action.Independent<MyModel> changeValue() {
			return () -> Arbitraries.strings().alpha().ofMinLength(1)
									.map(aString -> Transformer.transform(
										"setValue: " + aString,
										model -> model.setValue(aString)
									));
		}

		private Action.Independent<MyModel> nullify() {
			return Action.just("nullify", model -> model.setValue(null));
		}

	}

	@Example
	void usingActionThatIsNotDependentOrIndependentFails() {
		Action<String> neitherDependentNotIndependent = new Action<String>() {};

		Assertions.assertThatThrownBy(
			() -> ActionChain.actionChains(() -> "", neitherDependentNotIndependent)
		).isInstanceOf(JqwikException.class);
	}

	private Action.Independent<String> addX() {
		return Action.just("+x", model -> model + "x");
	}

	private Action.Independent<String> failing() {
		return Action.just(
			"failing", model -> {throw new RuntimeException("failing");}
		);
	}

	private Action.Independent<String> addY() {
		return Action.just("+y", model -> model + "y");
	}

	static class MyModel {

		public String value = "";

		MyModel setValue(String aString) {
			this.value = aString;
			return this;
		}

		@Override
		public String toString() {
			return String.format("MyModel[value=%S]", value);
		}
	}

}
