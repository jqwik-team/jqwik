package net.jqwik.docs.types;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;

class TypeArbitraryExamples {

	@Property
	@Report(Reporting.GENERATED)
	void aPersonsIsAlwaysValid(@ForAll @UseType Person aPerson) {
		Assertions.assertThat(aPerson.name).isNotBlank();
		Assertions.assertThat(aPerson.age).isBetween(0, 130);
	}

	@Property
	@Domain(People.class)
	@Report(Reporting.GENERATED)
	void aPartyOfPeopleCanBeGenerated(@ForAll @UseType Party aParty) {
		Assertions.assertThat(aParty.name).isNotBlank();
		// Assertions.assertThat(aParty.people).allMatch(
		// 	person -> !person.name.isEmpty()
		// );
	}

	@Property
	@Report(Reporting.GENERATED)
	void aPersonsFromTheFactoryIsAlways0(@ForAll("people") Person aPerson) {
		Assertions.assertThat(aPerson.name).isNotBlank();
		Assertions.assertThat(aPerson.age).isEqualTo(0);
	}

	@Provide
	Arbitrary<Person> people() {
		return Arbitraries.forType(Person.class).usePublicFactoryMethods();
	}

	@Property
	@Report(Reporting.GENERATED)
	@Domain(People.class)
	void aPersonsFromTheFactoryAndDomainStringsHasShortNameWithSpace(
		@ForAll @UseType(UseTypeMode.FACTORIES) Person aPerson
	) {
		Assertions.assertThat(aPerson.name).contains(" ");
		Assertions.assertThat(aPerson.name.length()).isBetween(5, 21);
	}

	static class People extends DomainContextBase {
		@Provide
		Arbitrary<String> strings() {
			return Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
		}

		@Provide
		Arbitrary<Integer> ints() {
			return Arbitraries.integers().between(0, 100);
		}
	}

}
