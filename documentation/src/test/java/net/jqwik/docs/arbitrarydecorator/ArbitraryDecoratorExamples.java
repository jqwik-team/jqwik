package net.jqwik.docs.arbitrarydecorator;

import net.jqwik.api.*;

@PropertyDefaults(tries = 20)
public class ArbitraryDecoratorExamples {

	@Property
	void complexNumbers(@ForAll("complex") ComplexNumber number) {
		System.out.println(number);
	}

	@Provide
	Arbitrary<ComplexNumber> complex() {
		return new ComplexNumberArbitrary();
	}

	@Property
	void rationalNumbers(@ForAll("rationalOnly") ComplexNumber number) {
		System.out.println(number);
	}

	@Provide
	Arbitrary<ComplexNumber> rationalOnly() {
		return new ComplexNumberArbitrary().withoutImaginaryPart();
	}
}
