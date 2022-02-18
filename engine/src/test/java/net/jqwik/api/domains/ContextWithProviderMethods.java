package net.jqwik.api.domains;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

class ContextWithProviderMethods extends DomainContextBase {

	@Provide
	StringArbitrary numberStrings() {
		return Arbitraries.strings().numeric().ofLength(2);
	}

	@Provide
		//("numbers") // having a value will produce warning log entry
	Arbitrary<Character> numberChars() {
		return Arbitraries.integers().between(0, 9).map(i -> Character.forDigit(i, 10));
	}

	@Provide
	Arbitrary<List<String>> listsOfSize3(TypeUsage targetType) {
		TypeUsage innerTarget = targetType.getTypeArgument(0);
		if (innerTarget.isOfType(String.class)) {
			Arbitrary<String> inner = Arbitraries.defaultFor(innerTarget);
			return inner.list().ofSize(3);
		}
		return null;
	}

	@Provide
	Arbitrary<List<LocalDate>> listsOfDates() {
		return Arbitraries.just(LocalDate.now()).list().ofSize(1);
	}

	@Provide
	Arbitrary<Tuple2<Integer, Integer>> tuple2OfInts() {
		return Arbitraries.integers().tuple2();
	}

	// This should not be applied to target type Tuple2<Integer, Integer>
	@Provide
	Arbitrary<Tuple2<String, String>> tuple2OfStrings() {
		return Arbitraries.strings().tuple2();
	}

	@Provide
	Arbitrary<Tuple3<Integer, Integer, Integer>> tuple3OfInts() {
		return Arbitraries.integers().tuple3();
	}

	//@Provide
	String shouldProduceAWarningLogEntry() {
		return "hello";
	}

}
