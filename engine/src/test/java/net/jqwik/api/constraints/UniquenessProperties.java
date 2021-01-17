package net.jqwik.api.constraints;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

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
		boolean listsWithByClause(@ForAll @Uniqueness(by = GetLength.class) List<String> aStringList) {
			return hasNoDuplicates(aStringList, new GetLength());
		}

		private class GetLength implements Function<Object, Object> {
			@Override
			public Object apply(Object o) {
				return ((String) o).length();
			}
		}

		@Property
		boolean listsNotFromListArbitraryUsePlainFilter(@ForAll("listOfStrings") @Uniqueness List<String> aStringList) {
			return hasNoDuplicates(aStringList, Function.identity());
		}

		@Provide
		Arbitrary<List<String>> listOfStrings() {
			return Arbitraries.of(
					Arrays.asList("a", "b", "c"),
					Arrays.asList("b", "b", "x"),
					Arrays.asList("x", "y", "z")
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
			return new HashSet<>(Arrays.asList(strings));
		}

		private class GetFirstTwoChars implements Function<Object, Object> {
			@Override
			public Object apply(Object o) {
				return ((String) o).substring(0, 2);
			}
		}

	}

	private boolean hasNoDuplicates(Collection<?> collection, Function<Object, Object> by) {
		Set<Object> set = collection.stream().map(by).collect(Collectors.toSet());
		return set.size() == collection.size();
	}
}
