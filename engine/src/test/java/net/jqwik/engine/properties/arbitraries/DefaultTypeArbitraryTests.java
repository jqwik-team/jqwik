package net.jqwik.engine.properties.arbitraries;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

@Group
@Label("DefaultTypeArbitrary")
class DefaultTypeArbitraryTests {

	@Group
	class DirectUses {

		@Example
		void useConstructorWithoutParameter() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(String.class.getConstructor());

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aString -> {return aString.equals("");}
			);
		}

		@Example
		void useSingleFactoryWithoutParameter() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aString -> {return aString.equals("a string");}
			);
		}

		@Example
		void twoCreatorsAreUsedRandomly() throws NoSuchMethodException {

			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringFromNoParams"))
					.use(String.class.getConstructor());

			RandomGenerator<String> generator = typeArbitrary.generator(1000);

			assertAllGenerated(
				generator,
				aString -> aString.equals("") || aString.equals("a string")
			);

			assertAtLeastOneGeneratedOf(generator, "", "a string");
		}

		@Example
		void exceptionsDuringCreationAreIgnored() throws NoSuchMethodException {
			TypeArbitrary<String> typeArbitrary =
				new DefaultTypeArbitrary<>(String.class)
					.use(Samples.class.getDeclaredMethod("stringWithRandomException"));

			RandomGenerator<String> generator = typeArbitrary.generator(1000);

			assertAllGenerated(
				generator,
				aString -> {
					return aString.equals("a string");
				}
			);
		}

		@Example
		void useConstructorWithOneParameter() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@Example
		void useConstructorWithTwoParameters() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getConstructor(String.class, int.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@Example
		void useFactoryMethodWithTwoParameters() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.use(Person.class.getDeclaredMethod("create", int.class, String.class));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void reusingCreatorsIsIgnored() throws NoSuchMethodException {

			DefaultTypeArbitrary<String> typeArbitrary =
				(DefaultTypeArbitrary) new DefaultTypeArbitrary<>(String.class)
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
		void willUseAllPublicConstructorsAndFactoryMethods() {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class).useDefaults();

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}

		@Example
		void isOverwrittenByDirectUse() throws NoSuchMethodException {
			TypeArbitrary<Person> typeArbitrary =
				new DefaultTypeArbitrary<>(Person.class)
					.useDefaults()
					.use(Samples.class.getDeclaredMethod("personFromNoParams"));

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> {return aPerson.toString().equals("a person");}
			);
		}

		@Example
		void onAbstractClassUsesOnlyFactoryMethods() {
			TypeArbitrary<Animal> typeArbitrary =
				new DefaultTypeArbitrary<>(Animal.class).useDefaults();

			assertAllGenerated(
				typeArbitrary.generator(1000),
				animal -> animal.toString().startsWith("Cat") || animal.toString().startsWith("Dog")
			);
		}

		@Example
		void onInterfaceUsesOnlyFactoryMethods() {
			TypeArbitrary<Thing> typeArbitrary =
				new DefaultTypeArbitrary<>(Thing.class).useDefaults();

			assertAllGenerated(
				typeArbitrary.generator(1000),
				thing -> {return thing.toString().equals("Thing");}
			);
		}

	}

	@Group
	class UseConstructors {

		@Example
		void publicConstructorsOnly() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).usePublicConstructors();

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> {
					Assertions.assertThat(aPerson.string1).isEqualTo(aPerson.string2);
					Assertions.assertThat(aPerson.int1).isEqualTo(aPerson.int2);
				}
			);
		}

		@Example
		void allConstructors() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useAllConstructors();

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.string1.equals(aPerson.string2)
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.int1 == aPerson.int2
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> !aPerson.string1.equals(aPerson.string2)
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.int1 != aPerson.int2
			);
		}

		@Example
		void filterConstructors() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useConstructors(ctor -> ctor.getParameterCount() == 1);

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> {
					Assertions.assertThat(aPerson.string1).isEqualTo(aPerson.string2);
					Assertions.assertThat(aPerson.int1).isEqualTo(0);
					Assertions.assertThat(aPerson.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveConstructorsAreIgnored() {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary) new DefaultTypeArbitrary<>(Person.class).useAllConstructors();

			Assertions.assertThat(typeArbitrary.countCreators()).isEqualTo(2);
			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}
	}

	@Group
	class UseFactories {

		@Example
		void publicConstructorsOnly() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).usePublicFactoryMethods();

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> {
					Assertions.assertThat(aPerson.string1).isEqualTo(aPerson.string2);
					Assertions.assertThat(aPerson.int1).isEqualTo(aPerson.int2);
				}
			);
		}

		@Example
		void allConstructors() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useAllFactoryMethods();

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.string1.equals(aPerson.string2)
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.int1 == aPerson.int2
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> !aPerson.string1.equals(aPerson.string2)
			);

			assertAtLeastOneGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.int1 != aPerson.int2
			);
		}

		@Example
		void filterFactoryMethods() {
			TypeArbitrary<MyDomain> typeArbitrary =
				new DefaultTypeArbitrary<>(MyDomain.class).useFactoryMethods(method -> method.getParameterCount() == 1);

			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> {
					Assertions.assertThat(aPerson.string1).isEqualTo(aPerson.string2);
					Assertions.assertThat(aPerson.int1).isEqualTo(0);
					Assertions.assertThat(aPerson.int2).isEqualTo(0);
				}
			);
		}

		@SuppressWarnings("unchecked")
		@Example
		void recursiveFactoryMethodsAreIgnored() throws NoSuchMethodException {

			DefaultTypeArbitrary<Person> typeArbitrary =
				(DefaultTypeArbitrary) new DefaultTypeArbitrary<>(Person.class).useAllFactoryMethods();

			Assertions.assertThat(typeArbitrary.countCreators()).isEqualTo(1);
			assertAllGenerated(
				typeArbitrary.generator(1000),
				aPerson -> aPerson.toString().length() <= 100
			);
		}
	}

	@Group
	class ConfigurationErrors {
		@Example
		void typeArbitraryWithoutUseFailsOnGeneration() throws NoSuchMethodException {
			TypeArbitrary<String> typeArbitrary = new DefaultTypeArbitrary<>(String.class);

			Assertions.assertThatThrownBy(
				() -> typeArbitrary.generator(1000)
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
		void creatorWithParameterThatHasNoDefaultArbitraryWillThrowOnCreation() throws NoSuchMethodException {
			TypeArbitrary<Customer> typeArbitrary =
				new DefaultTypeArbitrary<>(Customer.class)
					.use(Customer.class.getConstructor(Person.class));

			RandomGenerator<Customer> generator = typeArbitrary.generator(1000);
			Assertions.assertThatThrownBy(
				() -> generator.next(SourceOfRandomness.current())
			).isInstanceOf(JqwikException.class);
		}

	}

	private static class Samples {

		private static Person personFromNoParams() {
			return Person.create(42, "a person");
		}

		private static String stringFromNoParams() {
			return "a string";
		}

		private static String stringWithRandomException() {
			if (SourceOfRandomness.current().nextDouble() > 0.5) {
				throw new AssertionError();
			}
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

		public static Person copy(Person person) {
			return new Person(person);
		}

		public Person(Person person) {
			this(person.name);
		}

		public static Person create(int age, String name) {
			return new Person(name, age);
		}

		public Person(String name, int age) {
			this(name);
		}

		public Person(String name) {
			if (name.length() > 100)
				throw new IllegalArgumentException();
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class Customer {
		public Customer(Person person) { }
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
