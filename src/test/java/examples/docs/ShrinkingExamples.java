package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

import static java.util.Collections.*;

class ShrinkingExamples {

	@Property(reporting = Reporting.FALSIFIED)
	boolean stringShouldBeShrunkToAA(@ForAll @AlphaChars String aString) {
		return aString.length() > 5 || aString.length() < 2;
	}

	@Property
	boolean shrinkingCanTakeLong(@ForAll("first") String first, @ForAll("second") String second) {
		String aString = first + second;
		return aString.length() > 5 || aString.length() < 2;
	}

	@Provide
	Arbitrary<String> first() {
		return Arbitraries.strings() //
				.withCharRange('a', 'z') //
				.ofMinLength(1) //
				.ofMaxLength(10) //
				.filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries.strings().withCharRange('0', '9').ofMinLength(0).ofMaxLength(10).filter(string -> string.length() >= 1);
	}

	@Property(shrinking = ShrinkingMode.OFF)
	void aPropertyWithLongShrinkingTimes(@ForAll List<Set<String>> list1, @ForAll List<Set<String>> list2) {
	}

	@Property(shrinking = ShrinkingMode.BOUNDED)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		Assume.that(anInt != Integer.MAX_VALUE);
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}

	static <E> List<E> brokenReverse(List<E> aList) {
		if (aList.size() < 4) {
			aList = new ArrayList<>(aList);
			reverse(aList);
		}
		return aList;
	}

	@Property
	boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
		Assume.that(!aList.isEmpty());
		List<Integer> reversed = brokenReverse(aList);
		return aList.get(0) == reversed.get(aList.size() - 1);
	}

	@Property
	boolean reverseShouldSwapFirstAndLast_Wildcard(@ForAll List<?> aList) {
		Assume.that(!aList.isEmpty());
		List<?> reversed = brokenReverse(aList);
		return aList.get(0).equals(reversed.get(aList.size() - 1));
	}



}
