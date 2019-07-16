package net.jqwik.api.constraints;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

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

	class SmallNumbers extends AbstractDomainContextBase {
		public SmallNumbers() {
			registerArbitrary(int.class, Arbitraries.integers().between(1, 99));
			registerArbitrary(long.class, Arbitraries.longs());
		}
	}

	private static class Person {
		private String name;

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
