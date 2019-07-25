import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Combinators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

public class Experiments {

	@Group
	class Nesterd {
		@Property(tries = 10)
		void listOfStrings(@ForAll List<@StringLength(5) String> list) {
//		System.out.println(list);
			assertThat(list).allSatisfy(s -> assertThat(s).hasSize(5));
		}
	}

	@Property(generation = GenerationMode.RANDOMIZED)
	void test(@ForAll("smallSet") Set<Integer> aSet) {
		System.out.println(aSet);
	}

	@Provide
	Arbitrary<Set<Integer>> smallSet() {
		return Arbitraries.of(1, 2, 3, 4, 5).set().ofSize(6);
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	boolean unique(@ForAll("keys") int aKey, @ForAll("lists") List<Integer> aList) {
		System.out.println(aKey);
		return true;
	}

	@Provide
	Arbitrary<List<Integer>> lists() {
		return  keys().list();
	}
	@Provide
	Arbitrary<Integer> keys() {
		return
			Arbitraries.oneOf(
				Arbitraries.integers().between(0, 100),
				Arbitraries.integers()
			).unique();
	}

	@Property
	void caseExperiments(@ForAll @AlphaChars String aString) {
//		Statistics.collect(aString.length() < 50);
		Cases.of("aString")
			 .match(aString.isEmpty(), "empty", () -> aString.length() == 0)
			 .match(aString.length() < 10, "small", () -> {
				 assertThat(!aString.contains("a"));
			 })
			 .match(aString.length() < 100, "long", () -> aString.length() < 1000)
			 .noMatch();
//			 .noMatch(() -> Assertions.fail("no match"));
	}

	@Property
	void stringsUtf16(@ForAll String aString) {
		// UTF8: Problem with D800
		System.out.println(aString.codePointAt(0));
		System.out.println(Character.isValidCodePoint(aString.codePointAt(0)));
		String converted = new String(aString.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
		System.out.println(converted.codePointAt(0));

		assertThat(converted).isEqualTo(aString);
	}

	@Property
	void functions(@ForAll Function<String, Integer> function) {
	}

	@Property
	@AddLifecycleHook(MyFailingProperty.class)
	@Report(Reporting.GENERATED)
	boolean succeed(@ForAll String aString) {
		Statistics.collect(aString.length() > 10);
		return true;
	}

	@Property
	boolean fail(@ForAll String function) {
		return false;
	}

	@Property
	@Label("oopsa")
	void fail2(@ForAll String aString) {
		assertThat(aString.length()).isLessThan(3);
	}

	@Property(tries = 10)
	void combineWithBuilder(@ForAll("people") Person aPerson) {
		System.out.println(aPerson);
	}

	@Provide
	Arbitrary<Person> people() {
		BuilderCombinator<PersonBuilder> builderCombinator = Combinators.withBuilder(() -> new PersonBuilder());
		Arbitrary<String> names = Arbitraries.strings().alpha().ofLength(10);
		Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
		return builderCombinator
				   .use(names).in(PersonBuilder::withName)
				   .use(ages).in(PersonBuilder::withAge)
				   .build(PersonBuilder::build);
	}

	private static class MyFailingProperty implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			property.execute();
			throw new AssertionError("poops");
		}
	}

	public static class Person {

		private final String name;
		private final int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {
			return String.format("%s (%s)", name, age);
		}
	}

	public static class PersonBuilder {

		private String name = "A name";
		private int age = 42;

		public PersonBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public PersonBuilder withAge(int age) {
			this.age = age;
			return this;
		}

		public Person build() {
			return new Person(name, age);
		}
	}
}

class Cases {

	private boolean matched = false;
	private String name;

	private Cases(String name) {
		this.name = name + ":";
	}

	public static Cases of(String name) {
		return new Cases(name);
	}

	public Cases match(boolean condition, String label, BooleanSupplier tester) {
		if (!matched && condition) {
			Statistics.collect(name, label);
			matched = true;
			if (tester.getAsBoolean() == false) {
				throw new org.opentest4j.AssertionFailedError(label + " failed");
			}
		}
		return this;
	}

	public Cases match(boolean condition, String label, Runnable tester) {
		if (!matched && condition) {
			matched = true;
			Statistics.collect(name, label);
			tester.run();
		}
		return this;
	}

	public void noMatch(String label) {
		noMatch(label, () -> {});
	}

	public void noMatch() {
		noMatch(() -> {});
	}

	public void noMatch(Runnable answer) {
		noMatch("<no match>", answer);
	}

	public void noMatch(String label, Runnable answer) {
		if (!matched) {
			Statistics.collect(name, label);
			answer.run();
		}
	}
}
