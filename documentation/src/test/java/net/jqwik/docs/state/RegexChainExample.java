package net.jqwik.docs.state;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

class RegexChainExample {

	@Property(tries = 10)
	void generateABPlusC(@ForAll("abplusc") Chain<String> regex) {
		String string = null;
		for (String value : regex) {
			string = value;
		}
		System.out.println(string);
		assertThat(string).matches("ab+c");
	}

	@Provide
	Arbitrary<Chain<String>> abplusc() {
		return Chains.chains(
			() -> "",
			new TransformerProvider<String>() {
				@Override
				public Predicate<String> precondition() {
					return s -> s.isEmpty();
				}

				@Override
				public Arbitrary<Transformer<String>> apply(Supplier<String> stringSupplier) {
					return just(s -> s + "a");
				}
			},
			new TransformerProvider<String>() {
				@Override
				public Predicate<String> precondition() {
					return s -> s.endsWith("a");
				}

				@Override
				public Arbitrary<Transformer<String>> apply(Supplier<String> stringSupplier) {
					return just(s -> s + "b");
				}
			},
			new TransformerProvider<String>() {
				@Override
				public Predicate<String> precondition() {
					return s -> s.endsWith("b");
				}

				@Override
				public Arbitrary<Transformer<String>> apply(Supplier<String> stringSupplier) {
					return frequency(
						Tuple.of(5, s -> s + "b"),
						Tuple.of(1, s -> s + "c")
					);
				}
			},
			new TransformerProvider<String>() {
				@Override
				public Predicate<String> precondition() {
					return s -> s.endsWith("c");
				}

				@Override
				public Arbitrary<Transformer<String>> apply(Supplier<String> stringSupplier) {
					return just(Transformer.endOfChain());
				}
			}
		).infinite().dontShrink();
	}

}
