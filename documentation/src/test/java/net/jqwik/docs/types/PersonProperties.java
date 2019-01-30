package net.jqwik.docs.types;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class PersonProperties {

	@Property(shrinking = ShrinkingMode.OFF)
	void aPersonsNameIsNeverEmpty(@ForAll("people") Person aPerson) {
		Assertions.assertThat(aPerson.toString()).isNotBlank();
	}

	@Provide
	Arbitrary<Person> people() {
		return Arbitraries.forType(Person.class);
	}
}
