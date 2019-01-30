package net.jqwik.docs.types;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

class TypeArbitraryExamples {

	@Property(shrinking = ShrinkingMode.OFF)
	@Report(Reporting.GENERATED)
	void aPersonsNameIsNeverEmpty(@ForAll("people") Person aPerson) {
		Assertions.assertThat(aPerson.toString()).isNotBlank();
	}

	@Provide
	Arbitrary<Person> people() {
		return Arbitraries.forType(Person.class);
	}

	@Property(shrinking = ShrinkingMode.OFF)
	@Report(Reporting.GENERATED)
	@Domain(People.class)
	void aPersonsNameIsNeverEmpty2(@ForAll Person aPerson) {
		Assertions.assertThat(aPerson.toString()).isNotBlank();
	}

	static class People extends AbstractDomainContextBase {
		public People() {
			registerArbitrary(String.class, Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10));
		}
	}

}
