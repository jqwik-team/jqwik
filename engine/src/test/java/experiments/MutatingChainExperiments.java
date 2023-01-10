package experiments;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

public class MutatingChainExperiments {

	static class SetMutatingChainState {
		final List<String> actualOps = new ArrayList<>();
		final Set<Integer> set = new HashSet<>();
		boolean hasPrints = false;

		@Override
		public String toString() {
			return "set=" + set + ", actualOps=" + actualOps;
		}
	}

	// This fails (in 1.7.2) although it shouldn't.
	// See https://github.com/jlink/jqwik/issues/428 for discussion on the topic
	@Property(shrinking = ShrinkingMode.FULL, seed = "7077187739734332001")
	void chainActionsAreProperlyDescribedEvenAfterChainExecution(@ForAll("setMutatingChain") ActionChain<SetMutatingChainState> chain) {
		chain = chain.withInvariant(
			state -> {
				if (state.hasPrints) {
					assertThat(state.actualOps).hasSizeLessThan(5);
				}
			}
		);

		SetMutatingChainState finalState = chain.run();

		assertThat(chain.transformations())
			.describedAs("chain.transformations() should be the same as the list of operations in finalState.actualOps, final state is %s", finalState.set)
			.isEqualTo(finalState.actualOps);
	}

	@Provide
	public ActionChainArbitrary<SetMutatingChainState> setMutatingChain() {
		return
			ActionChain
				.startWith(SetMutatingChainState::new)
				.withAction(
					1,
					Action.just("clear anyway", state -> {
						state.actualOps.add("clear anyway");
						state.set.clear();
						return state;
					})
				)
				// Below actions depend on the state to derive the transformations
				.withAction(
					1,
					(Action.Dependent<SetMutatingChainState>)
						state ->
							Arbitraries
								.just(
									state.set.isEmpty()
										? Transformer.noop()
										: Transformer.<SetMutatingChainState>mutate("clear " + state.set, set -> {
										state.actualOps.add("clear " + set.set);
										state.set.clear();
									})
								)
				)
				.withAction(
					4,
					(Action.Dependent<SetMutatingChainState>)
						state ->
							Arbitraries
								.integers()
								.between(1, 10)
								.map(i -> {
										 if (state.set.contains(i)) {
											 return Transformer.noop();
										 }
										 return Transformer.mutate("add " + i + " to " + state.set, newState -> {
											 newState.actualOps.add("add " + i + " to " + newState.set);
											 newState.set.add(i);
										 });
									 }
								)
				)
				.withAction(
					2,
					(Action.Dependent<SetMutatingChainState>)
						state ->
							state.set.isEmpty() ? Arbitraries.just(Transformer.noop()) :
								Arbitraries
									.of(state.set)
									.map(i -> {
											 if (!state.set.contains(i)) {
												 throw new IllegalStateException("The set does not contain " + i + ", current state is " + state);
											 }
											 return Transformer.mutate("print " + i + " from " + state.set, newState -> {
												 newState.actualOps.add("print " + i + " from " + newState.set);
												 newState.hasPrints = true;
											 });
										 }
									)
				)
				.withMaxTransformations(7);
	}

}

