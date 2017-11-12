package examples.docs;

import java.util.List;

import net.jqwik.api.*;

class FlatMappingExamples {

	@Property(reporting = ReportingMode.GENERATED)
	boolean fixedSizedStrings(@ForAll("listsOfEqualSizedStrings")List<String> lists) {
		return lists.stream().distinct().count() == 1;
	}

	@Provide
	Arbitrary<List<String>> listsOfEqualSizedStrings() {
		Arbitrary<Integer> integers2to5 = Arbitraries.integers(2, 5);
		return integers2to5.flatMap(stringSize -> {
			Arbitrary<String> strings = Arbitraries.strings('a', 'z', stringSize, stringSize);
			return Arbitraries.listOf(strings);
		});
	}
}
