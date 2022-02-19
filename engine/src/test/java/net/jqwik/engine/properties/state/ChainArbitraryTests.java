package net.jqwik.engine.properties.state;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

class ChainArbitraryTests {

	@Example
	void simpleChain(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			intSupplier -> {
				int last = intSupplier.get();
				return Arbitraries.integers().between(last, last + 10);
			}
		);

		Chain<Integer> chain = chains.generator(100).next(random).value();

		Iterator<Integer> iterator = chain.start();
		int last = 1;
		while (iterator.hasNext()) {
			int next = iterator.next();
			assertThat(next).isGreaterThanOrEqualTo(last);
			last = next;
		}
	}
}
