package examples.packageWithSeveralContainers;

import net.jqwik.api.*;

public class PropertyTests {

	@Property
	boolean isTrue() {
		return true;
	}

	@Property
	Boolean isAlsoTrue() {
		return Boolean.TRUE;
	}

	@Property
	boolean isFalse() {
		return false;
	}

	@Property
	boolean allNumbersAreZero(@ForAll int aNumber) {
		return aNumber == 0;
	}

	@Property
	String incompatibleReturnType() {
		return "aString";
	}

	@Property(tries = 100, seed = 42L)
	boolean withEverything(@ForAll("lessThan5") int aNumber, @ForAll("shorterThan5") String aString) {
		Assume.that(aNumber == aString.length());
		return aString.length() == aNumber;
	}

	@Provide
	Arbitrary<Integer> lessThan5() {
		return Arbitraries.integers().between(0, 4);
	}

	@Provide
	Arbitrary<String> shorterThan5() {
		return Arbitraries.strings().withChars(new char[]{'a', 'b', 'c'}).ofMinLength(0).ofMaxLength(4);
	}

}
