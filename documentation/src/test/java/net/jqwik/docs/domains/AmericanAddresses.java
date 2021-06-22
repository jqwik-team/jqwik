package net.jqwik.docs.domains;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

public class AmericanAddresses extends DomainContextBase {

	@Provide
	Arbitrary<Street> streets() {
		Arbitrary<String> streetName = capitalizedWord(30);
		Arbitrary<String> streetType = Arbitraries.of("Street", "Avenue", "Road", "Boulevard");
		return Combinators.combine(streetName, streetType).as((n, t) -> n + " " + t).map(Street::new);
	}

	@Provide
	Arbitrary<Integer> streetNumbers() {
		return Arbitraries.integers().between(1, 999);
	}

	@Provide
	Arbitrary<State> states() {
		return Arbitraries.of(State.class);
	}

	@Provide
	Arbitrary<City> cities() {
		Arbitrary<String> name = capitalizedWord(25);
		Arbitrary<State> state = Arbitraries.defaultFor(State.class);
		Arbitrary<String> zip = Arbitraries.strings().numeric().ofLength(5);
		return Combinators.combine(name, state, zip).as(City::new);
	}

	@Provide
	Arbitrary<Address> addresses() {
		Arbitrary<Street> streets = Arbitraries.defaultFor(Street.class);
		Arbitrary<City> cities = Arbitraries.defaultFor(City.class);
		return Combinators.combine(streets, streetNumbers(), cities).as(Address::new);
	}

	private Arbitrary<String> capitalizedWord(int maxLength) {
		Arbitrary<Character> capital = Arbitraries.chars().range('A', 'Z');
		Arbitrary<String> rest = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(maxLength - 1);
		return Combinators.combine(capital, rest).as((c, r) -> c + r);
	}
}
