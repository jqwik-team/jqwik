package net.jqwik.docs;

import net.jqwik.api.*;

class ShrinkingFilterExamples {

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	// Different seeds result in different shrinking results
	boolean shrinkingCanTakeAWhile(@ForAll("first") String first, @ForAll("second") String second) {
		String aString = first + second;
		return aString.length() > 5 || aString.length() < 4;
	}

	@Provide
	Arbitrary<String> first() {
		return Arbitraries.strings()
						  .withCharRange('a', 'z')
						  .ofMinLength(1)
						  .ofMaxLength(10)
						  .filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries.strings()
						  .withCharRange('0', '9')
						  .ofMinLength(0)
						  .ofMaxLength(10)
						  .filter(string -> string.length() >= 1);
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	boolean shouldShrinkToBAH(@ForAll("aVariableString") String aString) {
		return aString.length() > 4 || aString.length() < 3;
	}

	@Provide()
	Arbitrary<String> aVariableString() {
		return Arbitraries.strings()
						  .withCharRange('a', 'z')
						  .ofMinLength(1)
						  .ofMaxLength(10)
						  .filter(string -> string.endsWith("h"))
						  .filter(string -> string.charAt(0) > 'a');
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	boolean withAssumption_shouldShrinkToCCH(@ForAll("aVariableString") String aString) {
		Assume.that(!aString.contains("a") && !aString.contains("b"));
		return aString.length() > 4 || aString.length() < 3;
	}

}
