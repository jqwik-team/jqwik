package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.assertj.core.api.Assertions.*;

class FlatMappingExamples {

	@Property
	boolean arbitrarySizedStrings(@ForAll("arbitraryString") String aString) {
		int length = aString.length();
		// Should fail and shrink to ["aaa"]
		return length > 6 || length < 3;
	}

	@Provide
	Arbitrary<String> arbitraryString() {
		return Arbitraries.integers().between(1, 10)
						  .flatMap(size -> Arbitraries.strings().withCharRange('a', 'z')
													  .ofMinLength(size).ofMaxLength(size));
	}

	@Property
	@Report(Reporting.GENERATED)
	boolean fixedSizedStringLists(@ForAll("listsOfEqualSizedStrings") List<String> strings) {
		Assume.that(!strings.isEmpty());
		return strings.stream().map(String::length).distinct().count() == 1;
	}

	@Provide
	Arbitrary<List<String>> listsOfEqualSizedStrings() {
		Arbitrary<Integer> integers2to5 = Arbitraries.integers().between(2, 5);
		return integers2to5.flatMap(stringSize -> {
			Arbitrary<String> strings = Arbitraries.strings()
												   .withCharRange('a', 'z')
												   .ofMinLength(stringSize).ofMaxLength(stringSize);
			return strings.list();
		});
	}

	@Property
	@Report(Reporting.GENERATED)
	void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringBeginEnd) {
		String aString = stringBeginEnd.get1();
		int begin = stringBeginEnd.get2();
		int end = stringBeginEnd.get3();
		assertThat(aString.substring(begin, end).length()).isEqualTo(end - begin);
	}

	@Provide
	Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd() {
		Arbitrary<String> stringArbitrary = Arbitraries.strings()
													   .withCharRange('a', 'z')
													   .ofMinLength(2).ofMaxLength(20);
		return stringArbitrary
				   .flatMap(aString -> Arbitraries.integers().between(0, aString.length())
												  .flatMap(end -> Arbitraries.integers().between(0, end)
																			 .map(begin -> Tuple.of(aString, begin, end))));
	}

	@Property
	@Report(Reporting.GENERATED)
	void substringLengthWithImplicitFlatMapping(@ForAll("stringWithBeginEnd2") Tuple3<String, Integer, Integer> stringBeginEnd) {
		String aString = stringBeginEnd.get1();
		int begin = stringBeginEnd.get2();
		int end = stringBeginEnd.get3();
		assertThat(aString.substring(begin, end).length()).isEqualTo(end - begin);
	}

	@Provide
	Arbitrary<String> simpleStrings() {
		return Arbitraries.strings()
						  .withCharRange('a', 'z')
						  .ofMinLength(2).ofMaxLength(20);
	}

	@Provide
	Arbitrary<Tuple2<String, Integer>> stringWithEnd(@ForAll("simpleStrings") String aString) {
		return Arbitraries.integers().between(0, aString.length())
						  .map(end -> Tuple.of(aString, end));
	}

	@Provide
	Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd2(@ForAll("stringWithEnd") Tuple2<String, Integer> stringWithEnd) {
		String aString = stringWithEnd.get1();
		int end = stringWithEnd.get2();
		return Arbitraries.integers().between(0, end)
						  .map(begin -> Tuple.of(aString, begin, end));
	}

}
