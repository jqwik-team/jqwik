package examples.docs;

import java.util.List;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

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
	boolean aStringWithItsMaxLength(@ForAll("stringWithMaxLength")Tuple2<Integer, String> lengthAndString) {
		int maxLength = lengthAndString.get1();
		String aString = lengthAndString.get2();
		return aString.length() <= maxLength;
	}

	@Provide
	Arbitrary<Tuple2<Integer, String>> stringWithMaxLength() {
		Arbitrary<Integer> integers2to5 = Arbitraries.integers(2, 15);
		return integers2to5.flatMap(stringSize -> Arbitraries.strings('a', 'z', 0, stringSize)
															 .map(string -> Tuple.of(stringSize, string)));
	}
}
