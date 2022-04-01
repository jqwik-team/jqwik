package net.jqwik.docs.state;

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
			stringSupplier -> {
				String value = stringSupplier.get();
				if (value.isEmpty()) {
					return just(s -> s + "a");
				}
				if (value.endsWith("a")) {
					return just(s -> s + "b");
				}
				if (value.endsWith("b")) {
					return frequency(
						Tuple.of(5, s -> s + "b"),
						Tuple.of(1, s -> s + "c")
					);
				}
				return just(Transformer.endOfChain());
			}
		).infinite().dontShrink();
	}

}
