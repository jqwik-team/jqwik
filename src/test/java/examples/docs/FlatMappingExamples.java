package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.Tuples.*;
import org.assertj.core.api.*;

import java.util.*;

class FlatMappingExamples {

	@Property
	boolean arbitrarySizedStrings(@ForAll("arbitraryString") String aString) {
		int length = aString.length();
		// Should fail and shrink to ["aaa"]
		return length > 6 || length < 3;
	}

	@Provide
	Arbitrary<String> arbitraryString() {
		return Arbitraries.integers(1, 10)
			.flatMap(size -> Arbitraries.strings('a', 'z', size, size));
	}

	@Property(reporting = Reporting.GENERATED)
	boolean fixedSizedStringLists(@ForAll("listsOfEqualSizedStrings")List<String> strings) {
		Assume.that(!strings.isEmpty());
		return strings.stream().map(String::length).distinct().count() == 1;
	}

	@Provide
	Arbitrary<List<String>> listsOfEqualSizedStrings() {
		Arbitrary<Integer> integers2to5 = Arbitraries.integers(2, 5);
		return integers2to5.flatMap(stringSize -> {
			Arbitrary<String> strings = Arbitraries.strings('a', 'z', stringSize, stringSize);
			return Arbitraries.listOf(strings);
		});
	}

	@Property(reporting = Reporting.GENERATED)
	void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringBeginEnd) {
		String aString = stringBeginEnd.get1();
		int begin = stringBeginEnd.get2();
		int end = stringBeginEnd.get3();
		Assertions.assertThat(aString.substring(begin, end).length()) //
			.isEqualTo(end - begin);
	}

	@Provide
	Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd() {
		Arbitrary<String> stringArbitrary = Arbitraries.strings('a', 'z', 2, 20);
		return stringArbitrary //
			.flatMap(aString -> Arbitraries.integers(0, aString.length()) //
				.flatMap(end -> Arbitraries.integers(0, end) //
					.map(begin -> Tuples.tuple(aString, begin, end))));
	}
}
