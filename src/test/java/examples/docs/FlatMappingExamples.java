package examples.docs;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import org.assertj.core.api.*;

import java.util.*;

class FlatMappingExamples {

	@Property(reporting = ReportingMode.GENERATED)
	boolean fixedSizedStrings(@ForAll("listsOfEqualSizedStrings")List<String> strings) {
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

	@Property(reporting = ReportingMode.GENERATED)
	void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringFromTo) {
		String aString = stringFromTo.get1();
		int begin = stringFromTo.get2();
		int end = stringFromTo.get3();
		Assertions.assertThat(aString.substring(begin, end).length()) //
			.isEqualTo(end - begin);
	}

	@Provide
	Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd() {
		Arbitrary<String> stringArbitrary = Arbitraries.strings('a', 'z', 2, 20);
		return stringArbitrary //
			.flatMap(aString -> Arbitraries.integers(0, aString.length()) //
				.flatMap(end -> Arbitraries.integers(0, end) //
					.map(begin -> Tuple.of(aString, begin, end))));
	}
}
