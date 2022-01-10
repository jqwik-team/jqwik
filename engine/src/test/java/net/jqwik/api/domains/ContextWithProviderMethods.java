package net.jqwik.api.domains;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
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
	ListArbitrary<?> listsOfSize3(TypeUsage targetType) {
		TypeUsage innerTarget = targetType.getTypeArgument(0);
		try {
			Arbitrary<Object> inner = Arbitraries.defaultFor(innerTarget);
			return inner.list().ofSize(3);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return null;
		}
	}

	@Provide
	Arbitrary<List<LocalDate>> listsOfDates() {
		return Arbitraries.just(LocalDate.now()).list().ofSize(1);
	}

	//@Provide
	String shouldProduceAWarningLogEntry() {
		return "hello";
	}

}
