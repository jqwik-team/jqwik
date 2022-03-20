package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

class ActionChainArbitraryTests {

	@Example
	void deterministicChainCanBeRun(@ForAll("x10") ActionChain<String> chain) {
		assertThat(chain.finalState()).isNotPresent();
		String result = chain.run();

		assertThat(chain.finalState()).isPresent();
		chain.finalState().ifPresent(s -> assertThat(s).isEqualTo(result));
		assertThat(chain.runActions().size()).isEqualTo(10);
		assertThat(result).isEqualTo("xxxxxxxxxx");
	}

	@Provide
	ActionChainArbitrary<String> x10() {
		return Chains.actionChains(() -> "", addX()).withMaxActions(10);
	}

	@Property(tries = 10)
	void chainChoosesBetween(@ForAll("xOrY") ActionChain<String> chain) {
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

	private Action<String> addX() {
		return Action.transform(model -> model + "x");
	}

	private Action<String> addY() {
		return Action.transform(model -> model + "y");
	}

}
