package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class DefaultTypeArbitraryTests {

	@Group
	@Label("useDefaults")
	class UseDefaults {

		@Example
		void willUseAllPublicConstructorsAndFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Person> typeArbitrary = new DefaultTypeArbitrary<>(Person.class);

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> !aPerson.name.equals("non-public-factory-name")
			);
		}

		@Example
		void onAbstractClassUsesOnlyFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Animal> typeArbitrary = new DefaultTypeArbitrary<>(Animal.class);

			checkAllGenerated(
				typeArbitrary,
				random,
				animal -> animal.toString().startsWith("Cat") || animal.toString().startsWith("Dog")
			);
		}

		@Example
		void onInterfaceUsesOnlyFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Thing> typeArbitrary = new DefaultTypeArbitrary<>(Thing.class);

			checkAllGenerated(
				typeArbitrary,
				random,
				thing -> thing.toString().equals("Thing")
			);
		}

	}

	@Group
	class UseConstructors {

		@Example
		void publicConstructorsOnly(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).usePublicConstructors();

			assertAllGenerated(
				typeArbitrary.generator(1000, true),
				random,
				aDomain -> {
					assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					assertThat(aDomain.int1).isEqualTo(aDomain.int2);
				}
			);
		}

		@Example
		void allConstructors(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useAllConstructors();

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.string1.equals(aPerson.string2)
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.int1 == aPerson.int2
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> !aPerson.string1.equals(aPerson.string2)
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.int1 != aPerson.int2
			);
		}

		@Example
		void filterConstructors(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useConstructors(ctor -> ctor.getParameterCount() == 1);

			assertAllGenerated(
				typeArbitrary.generator(1000, true),
				random,
				aDomain -> {
					assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					assertThat(aDomain.int1).isEqualTo(0);
					assertThat(aDomain.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveConstructorsAreIgnored(@ForAll Random random) {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary<Person>) new DefaultTypeArbitrary<>(Person.class).useAllConstructors();

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.name.length() <= 100
			);
		}
	}

	@Group
	class UseFactories {

		@Example
		void publicConstructorsOnly(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).usePublicFactoryMethods();

			assertAllGenerated(
				typeArbitrary.generator(1000, true),
				random,
				aDomain -> {
					assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					assertThat(aDomain.int1).isEqualTo(aDomain.int2);
				}
			);
		}

		@Example
		void allConstructors(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useAllFactoryMethods();

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.string1.equals(aPerson.string2)
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.int1 == aPerson.int2
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> !aPerson.string1.equals(aPerson.string2)
			);

			checkAtLeastOneGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.int1 != aPerson.int2
			);
		}

		@Example
		void filterFactoryMethods(@ForAll Random random) {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useFactoryMethods(method -> method.getParameterCount() == 1);

			assertAllGenerated(
				typeArbitrary.generator(1000, true),
				random,
				aDomain -> {
					assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					assertThat(aDomain.int1).isEqualTo(0);
					assertThat(aDomain.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveFactoryMethodsAreIgnored(@ForAll Random random) {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary<Person>) new DefaultTypeArbitrary<>(Person.class).useAllFactoryMethods();

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.name.length() <= 100
			);
		}
	}

	@Group
	class RecursiveUse {

		@Example
		void unresolvableSimpleTypeIsResolvedThroughTypeArbitrary(@ForAll Random random) {
			Arbitrary<Customer> typeArbitrary = new DefaultTypeArbitrary<>(Customer.class)
				.enableRecursion();

			assertAllGenerated(
				typeArbitrary,
				random,
				customer -> {
					assertThat(customer.person).isNotNull();
					assertThat(customer.tags).isNotNull();
				}
			);

			assertAtLeastOneGeneratedOf(
				typeArbitrary.generator(1000),
				random,
				Customer.defaultCustomer()
			);
		}

		@Example
		void resolveDeeperTypeRecursively(@ForAll Random random) {
			Arbitrary<Contract> typeArbitrary = new DefaultTypeArbitrary<>(Contract.class)
				.usePublicConstructors()
				.enableRecursion();

			assertAllGenerated(
				typeArbitrary,
				random,
				contract -> {
					assertThat(contract.customers).allMatch(c -> c instanceof Customer);
				}
			);
		}

	}

	@Group
	class ConfigurationErrors {
		@Example
		void typeArbitraryWithoutUseFailsOnGeneration() {
			TypeArbitrary<String> typeArbitrary = new DefaultTypeArbitrary<>(String.class);

			Assertions.assertThatThrownBy(
				() -> typeArbitrary.generator(1000, true)
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void creatorWithParameterThatHasNoDefaultArbitrary_willThrowException_whenGeneratorIsCreated(@ForAll Random random) {
			TypeArbitrary<Customer> typeArbitrary =
				new DefaultTypeArbitrary<>(Customer.class).usePublicConstructors();

			Assertions.assertThatThrownBy(
				() -> typeArbitrary.generator(1000, true)
			).isInstanceOf(JqwikException.class);
		}

	}

	@Group
	@PropertyDefaults(tries = 20)
	class Shrinking {

		@Property
		void simpleType(@ForAll Random random) {
			Arbitrary<Person> arbitrary = new DefaultTypeArbitrary<>(Person.class).usePublicConstructors();
			Person shrunkValue = falsifyThenShrink(arbitrary, random);

			assertThat(shrunkValue).isIn(
				new Person("", 0),
				new Person("", 99)
			);
		}

		@Property
		void recursiveType(@ForAll Random random) {
			Arbitrary<Customer> arbitrary = new DefaultTypeArbitrary<>(Customer.class).usePublicConstructors().enableRecursion();
			Customer shrunkValue = falsifyThenShrink(arbitrary, random);

			assertThat(shrunkValue).isIn(
				new Customer(new Person("", 0), Collections.emptyList()),
				new Customer(new Person("", 99), Collections.emptyList())
			);
		}

	}

	@Group
	class ExhaustiveGeneration {
		@Example
		void exhaustiveCombinationOfUsedArbitraries() {
			Arbitrary<Coordinates> arbitrary = new DefaultTypeArbitrary<>(Coordinates.class).usePublicConstructors();

			Optional<ExhaustiveGenerator<Coordinates>> optionalGenerator = arbitrary.exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Coordinates> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(256 * 256);

			assertThat(generator).contains(
				new Coordinates((byte) -128, (byte) -128),
				new Coordinates((byte) 127, (byte) 127),
				new Coordinates((byte) 0, (byte) 0)
			);
		}
	}

	@Group
	class EdgeCaseGeneration {
		@Example
		void combinationOfEdgeCasesOfUsedArbitraries() {
			Arbitrary<Coordinates> arbitrary = new DefaultTypeArbitrary<>(Coordinates.class).usePublicConstructors();

			Set<Coordinates> edgeCases = collectEdgeCaseValues(arbitrary.edgeCases());
			assertThat(edgeCases).hasSize(81);
			assertThat(edgeCases).contains(
				new Coordinates((byte) -128, (byte) -128),
				new Coordinates((byte) 127, (byte) 127),
				new Coordinates((byte) 0, (byte) 0)
			);
		}

		@Example
		void canBeSwitchedOff() {
			Arbitrary<Coordinates> arbitrary = new DefaultTypeArbitrary<>(Coordinates.class).usePublicConstructors().withoutEdgeCases();

			Set<Coordinates> edgeCases = collectEdgeCaseValues(arbitrary.edgeCases());
			assertThat(edgeCases).isEmpty();
		}
	}

	private static class Coordinates {
		private final byte x;
		private final byte y;

		public Coordinates(byte x, byte y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Coordinates position = (Coordinates) o;

			if (x != position.x) return false;
			return y == position.y;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}

		@Override
		public String toString() {
			return String.format("(%s,%s)", x, y);
		}
	}


	private static class Samples {

		private static Person personFromAge(int age) {
			if (age <= 0) {
				throw new AssertionError("No negative age");
			}
			return Person.create(age, "a person");
		}

		private static Person personFromNoParams() {
			return Person.create(42, "a person");
		}

		private static String stringFromNoParams() {
			return "a string";
		}

		private String nonStaticMethod() {
			return "a string";
		}
	}

	private interface Thing {
		static Thing aThing() {
			return new Thing() {
				@Override
				public String toString() {
					return "Thing";
				}
			};
		}
	}

	private static abstract class Animal {

		public static Cat aCat(String name) {
			return new Cat(name);
		}

		public static Dog aDog(String name) {
			return new Dog(name);
		}

		public static Object shouldNotBeCalled(String name) {
			return new Object();
		}

		private final String name;

		public Animal(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return String.format("%s named %s", getClass().getSimpleName(), name);
		}
	}

	private static class Cat extends Animal {
		public Cat(String name) {
			super(name);
		}
	}

	private static class Dog extends Animal {
		public Dog(String name) {
			super(name);
		}
	}

	private static class Person {
		private final String name;
		private final int age;

		static Person nonPublicFactory(int age) {
			return new Person("non-public-factory-name", -1);
		}

		public static Person copy(Person person) {
			return new Person(person);
		}

		public Person(Person person) {
			this(person.name);
		}

		public static Person create(int age, String name) {
			return new Person(name, age);
		}

		public Person(String name) {
			this(name, 99);
		}

		public Person(String name, int age) {
			if (name.length() > 100)
				throw new IllegalArgumentException();
			this.name = name;
			this.age = age;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Person person = (Person) o;

			if (age != person.age) return false;
			return name.equals(person.name);
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + age;
			return result;
		}

		@Override
		public String toString() {
			return String.format("%s(%s)", name, age);
		}
	}

	private static class Customer {
		final Person person;
		final List<String> tags;

		public static Customer defaultCustomer() {
			return new Customer(new Person("P", 42), Collections.emptyList());
		}

		public Customer(Person person, List<String> tags) {
			this.person = person;
			this.tags = tags;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Customer customer = (Customer) o;

			if (!person.equals(customer.person)) return false;
			return tags.equals(customer.tags);
		}

		@Override
		public int hashCode() {
			int result = person.hashCode();
			result = 31 * result + tags.hashCode();
			return result;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("Customer{");
			sb.append("person=").append(person);
			sb.append(", tags=").append(tags);
			sb.append('}');
			return sb.toString();
		}
	}

	private static class Contract {
		final List<Customer> customers = new ArrayList<>();

		public static Contract aContract() {
			return new Contract(Customer.defaultCustomer(), Customer.defaultCustomer(), Customer.defaultCustomer());
		}

		private Contract(Customer c1, Customer c2, Customer c3) {
			customers.add(c1);
			customers.add(c2);
			customers.add(c3);
		}

		public Contract(Customer c1, Customer c2) {
			customers.add(c1);
			customers.add(c2);
		}

		public Contract(List<Customer> customers) {
			this.customers.addAll(customers);
		}
	}

	private static class MyDomain {
		String string1;
		String string2;
		int int1;
		int int2;

		public static String notAFactoryMethod(String string1) {
			return string1 + string1;
		}

		public static MyDomain factory1(String string1) {
			return new MyDomain(string1);
		}

		public MyDomain(String string1) {
			this(string1, string1);
		}

		public static MyDomain factory2(String string1, int int1) {
			return new MyDomain(string1, int1);
		}

		public MyDomain(String string1, int int1) {
			this(string1, string1, int1, int1);
		}

		private static MyDomain factory3(String string1, String string2) {
			return new MyDomain(string1, string2);
		}

		private MyDomain(String string1, String string2) {
			this(string1, string2, 0, 0);
		}

		private static MyDomain factory4(String string1, String string2, int int1, int int2) {
			return new MyDomain(string1, string2, int1, int2);
		}

		private MyDomain(String string1, String string2, int int1, int int2) {
			this.string1 = string1;
			this.string2 = string2;
			this.int1 = int1;
			this.int2 = int2;
		}
	}

}
