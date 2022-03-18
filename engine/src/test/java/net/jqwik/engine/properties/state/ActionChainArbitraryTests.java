package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

class ActionChainArbitraryTests {

	@Disabled("implementation not finished")
	@Property
	void createdChainsDoTheirWork(@ForAll("xOrY") ActionChain<String> chain) {
		String result = chain.run();

		assertThat(chain.runActions().size()).isGreaterThanOrEqualTo(10);
		assertThat(result).hasSize(chain.runActions().size());
		assertThat(result.chars()).allMatch(c -> c == 'x' || c == 'y');
	}

	@SuppressWarnings("unchecked")
	@Provide
	ActionChainArbitrary<String> xOrY() {
		return Chains.actionChains(() -> "", addX(), addY());
	}

	private Action<String> addX() {
		return Action.transform(model -> model + "x");
	}

	private Action<String> addY() {
		return Action.transform(model -> model + "y");
	}

}
