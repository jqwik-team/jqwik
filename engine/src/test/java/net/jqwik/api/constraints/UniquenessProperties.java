package net.jqwik.api.constraints;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

import static java.util.Arrays.*;

@PropertyDefaults(tries = 100)
@Group
class UniquenessProperties {

	@Group
	class Lists {

		@Property
		boolean lists(@ForAll @Uniqueness List<String> aStringList) {
			return hasNoDuplicates(aStringList, Function.identity());
		}

		@Property
		boolean listsWithByClause(@ForAll @Uniqueness(by = GetStringLength.class) List<String> aStringList) {
			return hasNoDuplicates(aStringList, new GetStringLength());
		}

		@Property
		boolean listsNotFromListArbitraryUsePlainFilter(@ForAll("listOfStrings") @Uniqueness List<String> aStringList) {
			return hasNoDuplicates(aStringList, Function.identity());
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
		boolean setsWithByClause(
				@ForAll @Uniqueness(by = GetFirstTwoChars.class)
						Set<@StringLength(3) @AlphaChars String> aStringSet
		) {
			return hasNoDuplicates(aStringSet, new GetFirstTwoChars());
		}

		@Property
		boolean setsNotFromSetArbitraryUsePlainFilter(
				@ForAll("setsOfStrings") @Uniqueness(by = GetFirstTwoChars.class) Set<String> aStringList
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

		private Set<String> asSet(String ... strings) {
			return new HashSet<>(asList(strings));
		}

		private class GetFirstTwoChars implements Function<Object, Object> {
			@Override
			public Object apply(Object o) {
				return ((String) o).substring(0, 2);
			}
		}
	}

	@Group
	class ArraysTests {

		@Property
		boolean arrays(@ForAll @Uniqueness String[] aStringArray) {
			return hasNoDuplicates(asList(aStringArray), Function.identity());
		}

		@Property
		boolean arraysWithByClause(@ForAll @Uniqueness(by = GetStringLength.class) String[] aStringArray) {
			return hasNoDuplicates(asList(aStringArray), new GetStringLength());
		}

		@Property
		boolean arraysNotFromListArbitraryUsePlainFilter(@ForAll("arrayOfStrings") @Uniqueness String[] aStringArray) {
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
		boolean streams(@ForAll @Uniqueness Stream<String> stringStream) {
			return hasNoDuplicates(stringStream.collect(Collectors.toList()), Function.identity());
		}

		@Property
		boolean streamsWithByClause(@ForAll @Uniqueness(by = GetStringLength.class) Stream<String> stringStream) {
			return hasNoDuplicates(stringStream.collect(Collectors.toList()), new GetStringLength());
		}

		@Property
		boolean streamsNotFromListArbitraryUsePlainFilter(@ForAll("streamOfStrings") @Uniqueness Stream<String> stringStream) {
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

	private class GetStringLength implements Function<Object, Object> {
		@Override
		public Object apply(Object o) {
			return ((String) o).length();
		}
	}

	private boolean hasNoDuplicates(Collection<?> collection, Function<Object, Object> by) {
		Set<Object> set = collection.stream().map(by).collect(Collectors.toSet());
		return set.size() == collection.size();
	}
}
