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
		ActionChainArbitrary<String> chains =
			ActionChain.startWith(() -> "")
					   .addAction(addX())
					   .withMaxTransformations(10);

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
	void infiniteChain(@ForAll Random random) {
		Action.Independent<String> addEOC = Action.just(Transformer.endOfChain());
		ActionChainArbitrary<String> chains =
			ActionChain.startWith(() -> "")
					   .addAction(addX())
					   .addAction(addEOC)
					   .infinite();

		TestingSupport.assertAllGenerated(
			chains,
			random,
			chain -> {
				String result = chain.run();
				assertThat(chain.running()).isEqualTo(RunningState.SUCCEEDED);
				assertThat(chain.finalState()).isPresent();
				assertThat(chain.transformations().size()).isGreaterThanOrEqualTo(1);
				if (result.length() > 0) {
					assertThat(result.chars()).containsOnly((int) 'x');
				}
			}
		);
	}

	@Example
	void peekingIntoChain(@ForAll Random random) {
		ActionChainArbitrary<String> chains =
			ActionChain.startWith(() -> "")
					   .addAction(addX())
					   .withMaxTransformations(5);

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
		return ActionChain.startWith(() -> "")
						  .addAction(addX())
						  .addAction(failing())
						  .withMaxTransformations(30);
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
		return ActionChain.startWith(() -> "")
						  .addAction(addX())
						  .addAction(addY())
						  .withMaxTransformations(30);
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
		return ActionChain.startWith(() -> "")
						  .addAction(anyAZ)
						  .withMaxTransformations(30);
	}

	@Example
	void preconditionsInSeparateActionsAreConsidered(@ForAll Random random) {
		Action<String> x0to4 = Action.<String>when(s1 -> s1.length() < 5)
									 .just(s2 -> s2 + "x");
		Action<String> y5to9 = Action.<String>when(s -> s.length() >= 5)
									 .just(s1 -> s1 + "y");

		ActionChainArbitrary<String> chains =
			ActionChain.startWith(() -> "")
					   .addAction(x0to4)
					   .addAction(y5to9)
					   .withMaxTransformations(10);

		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

		String result = chain.run();
		assertThat(result).isEqualTo("xxxxxyyyyy");
	}

	@Example
	void usingEndOfChain(@ForAll Random random) {
		Action.Independent<String> x0to4 = Action.<String>when(s -> s.length() < 5)
												 .describeAs("addX")
												 .just(s -> s + "x");
		Action.Independent<String> end = Action.<String>when(s -> s.length() >= 5)
											   .just(Transformer.endOfChain());

		ActionChainArbitrary<String> chains =
			ActionChain.startWith(() -> "")
					   .addAction(x0to4)
					   .addAction(end)
					   .withMaxTransformations(10);
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
	class ConvenienceSubtypes {

		@Example
		@Label("Action.JustTransform")
		void justTransform(@ForAll Random random) {
			Action.Independent<String> x0to4 = new Action.JustTransform<String>() {
				@Override
				public boolean precondition(String state) {
					return state.length() < 5;
				}

				@Override
				public String transform(String state) {
					return state + "x";
				}
			};

			Action.Independent<String> y5to9 = new Action.JustTransform<String>() {
				@Override
				public boolean precondition(String state) {
					return state.length() >= 5;
				}

				@Override
				public String transform(String state) {
					return state + "y";
				}
			};
			ActionChainArbitrary<String> chains =
				ActionChain.startWith(() -> "")
						   .addAction(x0to4)
						   .addAction(y5to9)
						   .withMaxTransformations(10);

			ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

			String result = chain.run();
			assertThat(result).isEqualTo("xxxxxyyyyy");
		}

		@Example
		@Label("Action.JustMutate")
		void justMutate(@ForAll Random random) {
			Action.Independent<List<String>> x0to4 = new Action.JustMutate<List<String>>() {
				@Override
				public boolean precondition(List<String> state) {
					return state.size() < 5;
				}

				@Override
				public void mutate(List<String> state) {
					state.add("x");
				}
			};

			Action.Independent<List<String>> y5to9 = new Action.JustMutate<List<String>>() {
				@Override
				public boolean precondition(List<String> state) {
					return state.size() >= 5;
				}

				@Override
				public void mutate(List<String> state) {
					state.add("y");
				}
			};
			ActionChainArbitrary<List<String>> chains =
				ActionChain.<List<String>>startWith(ArrayList::new)
						   .addAction(x0to4)
						   .addAction(y5to9)
						   .withMaxTransformations(10);

			ActionChain<List<String>> chain = TestingSupport.generateFirst(chains, random);

			List<String> result = chain.run();
			assertThat(result).containsExactly("x", "x", "x", "x", "x", "y", "y", "y", "y", "y");
		}

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

			ActionChainArbitrary<List<Integer>> chains =
				ActionChain.<List<Integer>>startWith(ArrayList::new)
						   .addAction(clear)
						   .addAction(add)
						   .withMaxTransformations(10);

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

			ActionChainArbitrary<List<Integer>> chains =
				ActionChain.<List<Integer>>startWith(ArrayList::new)
						   .addAction(nothing)
						   .addAction(add)
						   .withMaxTransformations(10)
						   .improveShrinkingWith(changeOfListDetector);

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
			return ActionChain.<Set<Integer>>startWith(LinkedHashSet::new)
							  .addAction(
								  Action.just("clear", set -> {
									  set.clear();
									  return set;
								  }))
							  .addAction(addNumber)
							  .withMaxTransformations(10);
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
			return ActionChain.<List<Integer>>startWith(ArrayList::new)
							  .addAction(addInitialNumber)
							  .addAction(addSmallerNumber)
							  .withMaxTransformations(30);
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
				return ActionChain.startWith(MyModel::new)
								  .addAction(changeValue())
								  .addAction(nullify())
								  .withMaxTransformations(20);
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

		ActionChainArbitrary<String> actionChainArbitrary = ActionChain.startWith(() -> "");
		Assertions.assertThatThrownBy(
			() -> actionChainArbitrary.addAction(neitherDependentNotIndependent)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void usingWeightLessThan1Fails() {
		Action.Independent<String> addA = Action.just("addA", s -> s + "A");

		ActionChainArbitrary<String> actionChainArbitrary = ActionChain.startWith(() -> "");
		Assertions.assertThatThrownBy(
			() -> actionChainArbitrary.addAction(0, addA)
		).isInstanceOf(IllegalArgumentException.class);
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
