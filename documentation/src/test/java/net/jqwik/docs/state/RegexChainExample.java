package net.jqwik.docs.state;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static net.jqwik.api.Arbitraries.*;

class RegexChainExample {

	@Property
	void generateABPlusC(@ForAll("abplusc") Chain<String> regex) {
		String string = null;
		for (String value: regex) {
			string = value;
		}
		Assertions.assertThat(string).matches("ab+c");
	}

	@Provide
	Arbitrary<Chain<String>> abplusc() {
		return Chains.chains(
			() -> "",
			stringSupplier -> {
				String value = stringSupplier.get();
				if (true) { //value.isEmpty()) {
					return just(s -> s + "a");
				}
				return just(Transformer.endOfChain());
			}
		);
	}

}
