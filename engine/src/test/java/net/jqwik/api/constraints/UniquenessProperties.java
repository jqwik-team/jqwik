package net.jqwik.api.constraints;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

@PropertyDefaults(tries = 100)
class UniquenessProperties {

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

	private boolean hasNoDuplicates(List<?> aList, Function<Object, Object> by) {
		Set<Object> set = aList.stream().map(by).collect(Collectors.toSet());
		return set.size() == aList.size();
	}
}
