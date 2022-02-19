package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.Chain;
import net.jqwik.api.state.Chains;
import net.jqwik.testing.ShrinkingSupport;
import net.jqwik.testing.TestingFalsifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		assertThat(chain.maxSize()).isEqualTo(10);
		assertThat(chain.iterations().size()).isEqualTo(0);

		Iterator<Integer> iterator = chain.start();
		int last = 1;
		while (iterator.hasNext()) {
			int next = iterator.next();
			assertThat(next).isGreaterThanOrEqualTo(last);
			last = next;
		}

		assertThat(chain.iterations().size()).isEqualTo(10);
	}

	@Example
	void chainCanBeRerunWithSameValues(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			intSupplier -> {
				int last = intSupplier.get();
				return Arbitraries.integers().between(last, last + 10);
			}
		);

		Shrinkable<Chain<Integer>> chainShrinkable = chains.generator(100).next(random);

		Chain<Integer> chain = chainShrinkable.value();

		List<Integer> values1 = new ArrayList<>();
		for (Integer i : chain) {
			values1.add(i);
		}
		assertThat(chain.iterations()).isEqualTo(values1);

		List<Integer> values2 = new ArrayList<>();
		for (Integer i : chain) {
			values2.add(i);
		}
		assertThat(values2).isEqualTo(values1);

	}

	@Example
	void shrinkChainToEnd(@ForAll Random random) {
		Arbitrary<Chain<Integer>> chains = Chains.chains(
			() -> 1,
			intSupplier -> {
				int last = intSupplier.get();
				return Arbitraries.integers().between(last, last + 10);
			}
		).ofMaxSize(5);

		TestingFalsifier<Chain<Integer>> falsifier = chain -> {
			for (Integer integer : chain) {
				// consume iterator
			}
			return false;
		};
		Chain<Integer> chain = ShrinkingSupport.falsifyThenShrink(chains, random, falsifier);

		assertThat(chain.iterations()).hasSize(5);
		assertThat(chain.iterations()).contains(1, 1, 1, 1, 1);

	}
}
