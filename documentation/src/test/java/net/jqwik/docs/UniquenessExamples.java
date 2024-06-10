package net.jqwik.docs;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.docs.types.*;

@PropertyDefaults(tries = 100)
class UniquenessExamples {

	@Property
	void aListOfNonEmptyAlphaStringsWithSize5_annotationsOnly(
			@ForAll @Size(5) List<@AlphaChars @NotEmpty String> alphaList
	) {
		Assertions.assertThat(alphaList).hasSize(5);
		Assertions.assertThat(alphaList).allMatch(string -> !string.isEmpty());
	}

	@Property
	void uniqueInList(@ForAll @Size(5) @UniqueElements List<@IntRange(min = 0, max = 10) Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Property
	void listOfStringsTheFirstCharacterOfWhichMustDiffer(@ForAll("listOfUniqueStrings") List<String> listOfStrings) {
		System.out.println(listOfStrings);
	}

	@Provide
	Arbitrary<List<String>> listOfUniqueStrings() {
		return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10)
						  .list().ofMaxSize(25).uniqueElements(s -> Character.toLowerCase(s.charAt(0)));
	}

	@Property
	void listOfStringsTheFirstCharacterOfWhichMustBeUnique(
			@ForAll @Size(max = 25) @UniqueElements(by = FirstChar.class) List<@AlphaChars @StringLength(min = 1, max = 10) String> listOfStrings
	) {
		Iterable<Character> firstCharacters = listOfStrings.stream().map(s -> s.charAt(0)).collect(Collectors.toList());
		Assertions.assertThat(firstCharacters).doesNotHaveDuplicates();
	}

	private class FirstChar implements Function<String, Character> {
		@Override
		public Character apply(String aString) {
			return aString.charAt(0);
		}
	}

	@Property
	@Report(Reporting.GENERATED)
	void listOfPeopleWithUniqueNames(@ForAll("people") List<Person> people) {
		List<String> names = people.stream().map(p -> p.name).collect(Collectors.toList());
		Assertions.assertThat(names).doesNotHaveDuplicates();
	}

	@Provide
	Arbitrary<List<Person>> people() {
		Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(20);
		Arbitrary<Integer> ages = Arbitraries.integers().between(0, 120);

		Arbitrary<Person> persons = Combinators.combine(names, ages).as((name, age) -> new Person(name, age));
		return persons.list().uniqueElements(p -> p.name);
	};
}
