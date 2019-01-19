package net.jqwik.docs.domains;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

public class AddressDomain extends AbstractDomainContextBase {

	public AddressDomain() {
		registerArbitrary(Street.class, streets());
		registerArbitrary(Integer.class, streetNumbers());
		registerArbitrary(City.class, cities());
		registerArbitrary(Address.class, addresses());
	}

	private Arbitrary<Address> addresses() {
		return Combinators.combine(streets(), streetNumbers(), cities()).as(Address::new);
	}

	private Arbitrary<City> cities() {
		return Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(50).map(City::new);
	}

	private Arbitrary<Integer> streetNumbers() {
		return Arbitraries.integers().between(1, 999);
	}

	private Arbitrary<Street> streets() {
		return Arbitraries.strings().alpha().withChars(' ').ofMinLength(1).ofMaxLength(30).map(Street::new);
	}
}
