package net.jqwik.docs;

import java.lang.annotation.*;
import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

class ProvideMethodExamples {

	@Property
	boolean concatenatingStringWithInt(@ForAll("shortStrings") String aShortString, @ForAll("10 to 99") int aNumber) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Property
	boolean concatenatingStringWithInt2(@ForAll @From("shortStrings") String aShortString, @ForAll @From("10 to 99") int aNumber) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Property
	boolean joiningListOfStrings(@ForAll List<@From("shortStrings") String> listOfStrings) {
		String concatenated = String.join("", listOfStrings);
		return concatenated.length() <= 8 * listOfStrings.size();
	}

	@Provide
	Arbitrary<String> shortStrings() {
		return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(8);
	}

	@Provide("10 to 99")
	Arbitrary<Integer> numbers() {
		return Arbitraries.integers().between(10, 99);
	}

	@Property
	void largePrimes(@ForAll("favouritePrimes") @LargePrimes int aPrime) {
		System.out.println("prime = " + aPrime);
	}

	@Property
	void smallPrimes(@ForAll("favouritePrimes") int aPrime) {
		System.out.println("prime = " + aPrime);
	}

	@Provide
	Arbitrary<Integer> favouritePrimes(TypeUsage targetType) {
		if (targetType.findAnnotation(LargePrimes.class).isPresent()) {
			return Arbitraries.integers().greaterOrEqual(2).filter(this::isPrime);
		}
		return Arbitraries.of(3, 5, 7, 13, 17, 23, 41);
	}

	private boolean isPrime(int i) {
		if (i < 2) return false;
		if (i == 2) return true;
		if (i % 2 == 0) return false;
		for (int j = 3; j * j <= i; j += 2) {
			if (i % j == 0) return false;
		}
		return true;
	}
}

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@interface LargePrimes {
}
