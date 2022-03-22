package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.api.state.ActionChain.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

class ActionChainArbitraryTests {

	@Example
	void deterministicChainCanBeRun(@ForAll Random random) {
		ActionChainArbitrary<String> chains = Chains.actionChains(() -> "", addX()).withMaxActions(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);
		assertThat(chain.running()).isEqualTo(RunningState.NOT_RUN);
		assertThat(chain.finalState()).isNotPresent();
		String result = chain.run();

		assertThat(chain.running()).isEqualTo(RunningState.SUCCEEDED);
		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.runActions().size()).isEqualTo(10);
		assertThat(result).isEqualTo("xxxxxxxxxx");
	}

	@Example
	void peekingIntoChain(@ForAll Random random) {
		ActionChainArbitrary<String> chains = Chains.actionChains(() -> "", addX()).withMaxActions(5);

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

	@Property(tries = 10)
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
		return Chains.actionChains(() -> "", addX(), failing()).withMaxActions(30);
	}

	@Property(tries = 10)
	void chainChoosesBetweenTwoActions(@ForAll("xOrY") ActionChain<String> chain) {
		String result = chain.run();

		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.runActions().size()).isGreaterThanOrEqualTo(30);
		assertThat(result).hasSize(chain.runActions().size());
		assertThat(result).contains("x");
		assertThat(result).contains("y");
		assertThat(result.chars()).allMatch(c -> c == 'x' || c == 'y');
	}

	@Provide
	ActionChainArbitrary<String> xOrY() {
		return Chains.actionChains(() -> "", addX(), addY()).withMaxActions(30);
	}

	@Property(tries = 10)
	void chainUsesRandomizedAction(@ForAll("anyAtoZ") ActionChain<String> chain) {
		String result = chain.run();

		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.runActions().size()).isGreaterThanOrEqualTo(30);
		assertThat(result).hasSize(chain.runActions().size());
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
		return Chains.actionChains(() -> "", anyAZ).withMaxActions(30);
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
		).withMaxActions(10);
		ActionChain<String> chain = TestingSupport.generateFirst(chains, random);

		String result = chain.run();
		assertThat(result).isEqualTo("xxxxxyyyyy");
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

}
