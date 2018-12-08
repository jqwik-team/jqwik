package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

import static java.util.Collections.*;

class ShrinkingExamples {

	@Property @Report(Reporting.FALSIFIED)
	boolean stringShouldBeShrunkToAA(@ForAll @AlphaChars String aString) {
		return aString.length() > 5 || aString.length() < 2;
	}

	@Property(shrinking = ShrinkingMode.BOUNDED) @Report(Reporting.FALSIFIED)
	// Should shrink to 46341 - the smallest number whose square is bigger than Integer.MAX_VALUE
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
		return aList.get(0).equals(reversed.get(aList.size() - 1));
	}

	@Property
	boolean reverseShouldSwapFirstAndLast_Wildcard(@ForAll List<?> aList) {
		Assume.that(!aList.isEmpty());
		List<?> reversed = brokenReverse(aList);
		return aList.get(0).equals(reversed.get(aList.size() - 1));
	}

	@Property(seed = "-6868766892804735822") @Report(Reporting.FALSIFIED)
	boolean shouldShrinkTo101(@ForAll("numberStrings") String aNumberString) {
		return Integer.parseInt(aNumberString) % 2 == 0;
	}

	@Provide
	Arbitrary<String> numberStrings() {
		return Arbitraries.integers().between(100, 1000).map(String::valueOf);
	}


}
