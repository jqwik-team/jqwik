package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;
import net.jqwik.testing.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@Label("DefaultTypeArbitrary")
class DefaultTypeArbitraryTests {

	@Group
	class DirectUses {

		@Example
		void useConstructorWithoutParameter(@ForAll Random random) throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(String.class.getConstructor());

			checkAllGenerated(
				typeArbitrary,
				random,
				aString -> aString.equals("")
			);
		}

		@Example
		void useSingleFactoryWithoutParameter(@ForAll Random random) throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"));

			checkAllGenerated(
				typeArbitrary,
				random,
				aString -> aString.equals("a string")
			);
		}

		@Example
		void twoCreatorsAreUsedRandomly(@ForAll Random random) throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"))
					.use(String.class.getConstructor());

			RandomGenerator<String> generator = typeArbitrary.generator(1000, true);

			checkAllGenerated(
				generator,
				random,
				aString -> aString.equals("") || aString.equals("a string")
			);

			assertAtLeastOneGeneratedOf(generator, random, "", "a string");
		}

		@Example
		void exceptionsDuringCreationAreIgnored(@ForAll Random random) throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Samples.class.getDeclaredMethod("personFromAge", int.class));

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.age > 0
			);
		}

		@Example
		void useConstructorWithOneParameter(@ForAll Random random) throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class));

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.name.length() <= 100
			);
		}

		@Example
		void useConstructorWithTwoParameters(@ForAll Random random) throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class, int.class));

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@Example
		void useFactoryMethodWithTwoParameters(@ForAll Random random) throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getDeclaredMethod("create", int.class, String.class));

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void reusingCreatorsIsIgnored() throws NoSuchMethodException {

			DefaultTypeArbitrary<String> typeArbitrary =
				(DefaultTypeArbitrary<String>) new DefaultTypeArbitrary<>(String.class)
										   .use(String.class.getConstructor())
										   .use(String.class.getConstructor())
										   .use(Samples.class.getDeclaredMethod("stringFromNoParams"))
										   .use(Samples.class.getDeclaredMethod("stringFromNoParams"));

			Assertions.assertThat(typeArbitrary.countCreators()).isEqualTo(2);
		}
	}

	@Group
	@Label("useDefaults")
	class UseDefaults {

		@Example
		void willUseAllPublicConstructorsAndFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class).useDefaults();

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> !aPerson.name.equals("non-public-factory-name")
			);
		}

		@Example
		void isOverwrittenByDirectUse(@ForAll Random random) throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.useDefaults()
					.use(Samples.class.getDeclaredMethod("personFromNoParams"));

			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.name.equals("a person") && aPerson.age == 42
			);
		}

		@Example
		void onAbstractClassUsesOnlyFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Animal> typeArbitrary =
				new DefaultTypeArbitrary<>(Animal.class).useDefaults();

			checkAllGenerated(
				typeArbitrary,
				random,
				animal -> animal.toString().startsWith("Cat") || animal.toString().startsWith("Dog")
			);
		}

		@Example
		void onInterfaceUsesOnlyFactoryMethods(@ForAll Random random) {
			TypeArbitrary<Thing> typeArbitrary =
				new DefaultTypeArbitrary<>(Thing.class).useDefaults();

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
					Assertions.assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					Assertions.assertThat(aDomain.int1).isEqualTo(aDomain.int2);
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
					Assertions.assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					Assertions.assertThat(aDomain.int1).isEqualTo(0);
					Assertions.assertThat(aDomain.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveConstructorsAreIgnored(@ForAll Random random) {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary<Person>) new DefaultTypeArbitrary<>(Person.class).useAllConstructors();

			Assertions.assertThat(typeArbitrary.countCreators()).isEqualTo(2);
			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.toString().length() <= 100
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
					Assertions.assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					Assertions.assertThat(aDomain.int1).isEqualTo(aDomain.int2);
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
					Assertions.assertThat(aDomain.string1).isEqualTo(aDomain.string2);
					Assertions.assertThat(aDomain.int1).isEqualTo(0);
					Assertions.assertThat(aDomain.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveFactoryMethodsAreIgnored(@ForAll Random random) {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary<Person>) new DefaultTypeArbitrary<>(Person.class).useAllFactoryMethods();

			Assertions.assertThat(typeArbitrary.countCreators()).isEqualTo(2);
			checkAllGenerated(
				typeArbitrary,
				random,
				aPerson -> aPerson.toString().length() <= 100
			);
		}
	}

	@Group
	class RecursiveUse {

		@Example
		@Disabled
		void withRecursiveUse_UnresolvableTypesAreResolvedThroughTypeArbitrary(@ForAll Random random) {
			TypeArbitrary<Customer> typeArbitrary = new DefaultTypeArbitrary<>(Customer.class)
				.useDefaults()
				.allowRecursion();

			checkAllGenerated(
				typeArbitrary,
				random,
				customer -> customer.person instanceof Person
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
		void nonStaticMethodsAreNotSupported() {
			Assertions.assertThatThrownBy(
				() -> new DefaultTypeArbitrary<>(String.class)
						  .use(Samples.class.getDeclaredMethod("nonStaticMethod"))
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void creatorWithWrongReturnTypeIsNotSupported() {
			Assertions.assertThatThrownBy(
				() -> new DefaultTypeArbitrary<>(int.class)
						  .use(Samples.class.getDeclaredMethod("stringFromNoParams"))
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void creatorWithParameterThatHasNoDefaultArbitrary_willThrowException_whenGeneratedValueIsRequests() throws NoSuchMethodException {
			TypeArbitrary<Customer> typeArbitrary =
				new DefaultTypeArbitrary<>(Customer.class).useDefaults();

			RandomGenerator<Customer> generator = typeArbitrary.generator(1000, true);
			Assertions.assertThatThrownBy(
					() -> generator.next(SourceOfRandomness.current()).value()
			).isInstanceOf(JqwikException.class);
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
		public String toString() {
			return String.format("%s=%s", name, age);
		}
	}

	private static class Customer {
		final Person person;

		public Customer(Person person) {this.person = person;}
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
