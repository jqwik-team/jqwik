package examples.docs;

import net.jqwik.api.*;

class FlatCombinationExamples {

	@Property(reporting = Reporting.GENERATED)
	boolean fullNameHasTwoParts(@ForAll("fullName") String aName) {
		return aName.split(" ").length == 2;
	}

	@Provide
	Arbitrary<String> fullName() {
		Arbitrary<Integer> firstNameLength = Arbitraries.integers().between(2, 10);
		Arbitrary<Integer> lastNameLength = Arbitraries.integers().between(2, 10);
		return Combinators.combine(firstNameLength, lastNameLength).flatAs((fLength, lLength) -> {
			Arbitrary<String> firstName = Arbitraries.strings().alpha().ofLength(fLength);
			Arbitrary<String> lastName = Arbitraries.strings().alpha().ofLength(fLength);
			return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
		});
	}

	@Provide
	Arbitrary<String> fullName2() {
		// Same result as fullName() but without flat combination
		Arbitrary<String> firstName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
		Arbitrary<String> lastName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
		return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
	}

}
