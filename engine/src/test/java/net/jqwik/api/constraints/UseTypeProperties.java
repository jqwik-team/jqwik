package net.jqwik.api.constraints;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.constraints.UseTypeMode.*;

@Group
class UseTypeProperties {
	@Property
	boolean withoutValue(@ForAll @UseType Person aPerson) {
		return aPerson != null;
	}

	@Property
	boolean fromAllConstructorsAndFactories(
		@ForAll @UseType({CONSTRUCTORS, FACTORIES}) Person aPerson
	) {
		return aPerson != null;
	}

	@Property(tries = 20)
	@Domain(SmallNumbers.class)
	void useTypeShouldWorkRegardlessOfDomainContext(
		@ForAll int smallNumber,
		@ForAll @UseType Random random
	) {
		assertThat(smallNumber).isBetween(1, 99);
		assertThat(random).isNotNull();
	}

	class SmallNumbers extends DomainContextBase {
		@Provide
		Arbitrary<Integer> ints() {
			return Arbitraries.integers().between(1, 99);
		}

		@Provide
		Arbitrary<Long> longs() {
			return Arbitraries.longs();
		}
	}

	private static class Person {
		private final String name;

		public static Person create(String name) {
			return new Person("factory: " + name);
		}

		private static Person createPrivate(String name) {
			return new Person("private factory: " + name);
		}

		public Person(String name) {
			this.name = name;
		}

		private Person(String name, int ignore) {
			this("private ctor: " + name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
