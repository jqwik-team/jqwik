package net.jqwik.api.constraints;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static java.util.Arrays.*;

@PropertyDefaults(tries = 100)
@Group
class UniqueElementsProperties {

	@Group
	class Lists {

		@Property
		boolean lists(@ForAll @UniqueElements List<String> aStringList) {
			return hasNoDuplicates(aStringList, s -> s);
		}

		@Property
		boolean listsOfModuloIntegers(@ForAll @UniqueElements(by = Modulo13.class) List<@Positive Integer> integerList) {
			return hasNoDuplicates(integerList, i -> i % 13);
		}

		private class Modulo13 implements Function<Integer, Integer> {
			@Override
			public Integer apply(Integer integer) {
				return integer % 13;
			}
		}

		@Property
		boolean listsWithByClause(@ForAll @UniqueElements(by = GetStringLength.class) List<String> aStringList) {
			return hasNoDuplicates(aStringList, new GetStringLength());
		}

		@Property
		boolean listsNotFromListArbitraryUsePlainFilter(@ForAll("listOfStrings") @UniqueElements List<String> aStringList) {
			return hasNoDuplicates(aStringList, s -> s);
		}

		@Provide
		Arbitrary<List<String>> listOfStrings() {
			return Arbitraries.of(
					asList("a", "b", "c"),
					asList("b", "b", "x"),
					asList("x", "y", "z")
			);
		}
	}

	@Group
	class Sets {
		@Property
		boolean sets(@ForAll @UniqueElements Set<String> aStringSet) {
			// May seem unnecessary but catches:
			//   * ClassCastException or NullPointerException
			//   * Empty Arbitrary
			return hasNoDuplicates(aStringSet, s -> s);
		}

		@Property
		boolean setsWithByClause(
				@ForAll @UniqueElements(by = GetFirstTwoChars.class)
						Set<@StringLength(3) @AlphaChars String> aStringSet
		) {
			return hasNoDuplicates(aStringSet, new GetFirstTwoChars());
		}

		@Property
		boolean setsNotFromSetArbitrary(
			@ForAll("setsOfStrings") @UniqueElements Set<String> aStringSet
		) {
			// May seem unnecessary but catches:
			//   * ClassCastException or NullPointerException
			//   * Empty Arbitrary
			return hasNoDuplicates(aStringSet, s -> s);
		}

		@Property
		boolean setsNotFromSetArbitraryUsePlainFilter(
				@ForAll("setsOfStrings") @UniqueElements(by = GetFirstTwoChars.class) Set<String> aStringList
		) {
			return hasNoDuplicates(aStringList, new GetFirstTwoChars());
		}

		@Provide
		Arbitrary<Set<String>> setsOfStrings() {
			return Arbitraries.of(
					asSet("aaa", "aab", "abc"),
					asSet("aba", "baa", "daa"),
					asSet("abx", "cdx", "dex")
			);
		}

		private Set<String> asSet(String... strings) {
			return new LinkedHashSet<>(asList(strings));
		}

		private class GetFirstTwoChars implements Function<String, String> {
			@Override
			public String apply(String string) {
				return string.substring(0, 2);
			}
		}
	}

	@Group
	class ArraysTests {

		@Property
		boolean arrays(@ForAll @UniqueElements String[] aStringArray) {
			return hasNoDuplicates(asList(aStringArray), Function.identity());
		}

		@Property
		boolean arraysWithByClause(@ForAll @UniqueElements(by = GetStringLength.class) String[] aStringArray) {
			return hasNoDuplicates(asList(aStringArray), new GetStringLength());
		}

		@Property
		boolean arraysNotFromListArbitraryUsePlainFilter(@ForAll("arrayOfStrings") @UniqueElements String[] aStringArray) {
			return hasNoDuplicates(asList(aStringArray), Function.identity());
		}

		@Provide
		Arbitrary<String[]> arrayOfStrings() {
			return Arbitraries.of(
					new String[] {"a", "b", "c"},
					new String[] {"b", "b", "x"},
					new String[] {"x", "y", "z"}
			);
		}
	}

	@Group
	class Streams {

		@Property
		boolean streams(@ForAll @UniqueElements Stream<String> stringStream) {
			return hasNoDuplicates(stringStream.collect(Collectors.toList()), Function.identity());
		}

		@Property
		boolean streamsWithByClause(@ForAll @UniqueElements(by = GetStringLength.class) Stream<String> stringStream) {
			return hasNoDuplicates(stringStream.collect(Collectors.toList()), new GetStringLength());
		}

		@Property
		boolean streamsNotFromListArbitraryUsePlainFilter(@ForAll("streamOfStrings") @UniqueElements Stream<String> stringStream) {
			return hasNoDuplicates(stringStream.collect(Collectors.toList()), Function.identity());
		}

		@Provide
		Arbitrary<Stream<String>> streamOfStrings() {
			return Arbitraries.of(
					Stream.of("a", "b", "c"),
					Stream.of("b", "b", "x"),
					Stream.of("x", "y", "z")
			);
		}
	}

	@Group
	class Iterators {

		@Property
		boolean iterators(@ForAll @UniqueElements Iterator<String> iterator) {
			return hasNoDuplicates(toList(iterator), s -> s);
		}

		@Property
		boolean iteratorsWithByClause(@ForAll @UniqueElements(by = GetStringLength.class) Iterator<String> iterator) {
			return hasNoDuplicates(toList(iterator), new GetStringLength());
		}

		@Property
		boolean iteratorsNotFromListArbitraryUsePlainFilter(@ForAll("iteratorOfStrings") @UniqueElements Iterator<String> iterator) {
			return hasNoDuplicates(toList(iterator), s -> s);
		}

		@Provide
		Arbitrary<Iterator<String>> iteratorOfStrings() {
			return Arbitraries.of(
					asList("b", "b", "x").iterator(),
					asList("a", "b", "c").iterator(),
					asList("x", "y", "z").iterator()
			);
		}

		private List<String> toList(Iterator<String> iterator) {
			Iterable<String> iterable = () -> iterator;
			return StreamSupport
						   .stream(iterable.spliterator(), false)
						   .collect(Collectors.toList());
		}

	}

	private class GetStringLength implements Function<String, Integer> {
		@Override
		public Integer apply(String string) {
			return string.length();
		}
	}

	private <T extends @Nullable Object> boolean hasNoDuplicates(Collection<T> collection, Function<? super T, ?> by) {
		Set<Object> set = collection.stream().map(by).collect(Collectors.toSet());
		return set.size() == collection.size();
	}
}
