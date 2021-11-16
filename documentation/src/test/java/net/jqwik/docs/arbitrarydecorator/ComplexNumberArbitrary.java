package net.jqwik.docs.arbitrarydecorator;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class ComplexNumberArbitrary extends ArbitraryDecorator<ComplexNumber> {

	private boolean withImaginaryPart = true;

	@Override
	protected Arbitrary<ComplexNumber> arbitrary() {
		Arbitrary<Double> rationalPart = Arbitraries.doubles();
		Arbitrary<Double> imaginaryPart = withImaginaryPart ? Arbitraries.doubles() : Arbitraries.just(0.0);
		return Combinators.combine(rationalPart, imaginaryPart).as(ComplexNumber::new);
	}

	public ComplexNumberArbitrary withoutImaginaryPart() {
		this.withImaginaryPart = false;
		return this;
	}
}
