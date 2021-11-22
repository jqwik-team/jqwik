package net.jqwik.api.constraints;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.constraints.UseTypeMode.*;

@Group
class UseTypeProperties {
	@Property
	boolean simpleType(@ForAll @UseType Person aPerson) {
		return aPerson != null;
	}

	@Property
	boolean fromAllConstructorsAndFactories(
		@ForAll @UseType({CONSTRUCTORS, FACTORIES}) Person aPerson
	) {
		return aPerson != null;
	}

	@Property
	void nestedType(@ForAll @UseType Parties aParties) {
		assertThat(aParties).isNotNull();
		assertThat(aParties.p1).isInstanceOf(Person.class);
		assertThat(aParties.p2).isInstanceOf(Person.class);
	}

	@Property
	@ExpectFailure(failureType = CannotFindArbitraryException.class)
	void nestedTypeWithoutAllowRecursion_throwsException(@ForAll @UseType(allowRecursion = false) Parties aParties) {
	}

	@Property
	void embeddedInGenericType(@ForAll List<@UseType Person> people) {
		assertThat(people).isNotNull();
		assertThat(people).allMatch(p -> p instanceof Person);
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

	private static class Parties {
		final Person p1;
		final Person p2;

		public Parties(Person p1, Person p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}
