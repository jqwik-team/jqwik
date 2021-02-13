package net.jqwik.api.constraints;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

// TODO
@Disabled("Unique behaviour is currently broken")
class UniqueProperties {

	@Property
	void uniqueInList(@ForAll @Size(5) List<@IntRange(min = 0, max = 10) @Unique Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Property
	void uniqueInListWithNullableElements(@ForAll @Size(5) List<@IntRange(min = 0, max = 10) @WithNull @Unique Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt == null || anInt >= 0 && anInt <= 10);
	}

	@Property
	void uniqueMapped(@ForAll("mappedIntegerList") List<Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 20);
	}

	@Provide
	Arbitrary<List<Integer>> mappedIntegerList() {
		return Arbitraries.integers().between(0, 10).unique().map(i -> i * 2).list().ofSize(5);
	}

	@Property
	void uniqueFiltered(@ForAll("filteredIntegerList") List<Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 20);
	}

	@Provide
	Arbitrary<List<Integer>> filteredIntegerList() {
		return Arbitraries.integers().between(0, 20).unique().filter(i -> i % 2 == 0).list().ofSize(5);
	}

	@Property
	void uniqueInIterator(@ForAll @Size(5) Iterator<@IntRange(min = 0, max = 10) @Unique Integer> anIterator) {
		List<Integer> aList = new ArrayList<>();
		while (anIterator.hasNext()) {
			aList.add(anIterator.next());
		}
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Property
	void uniqueInArray(@ForAll("array") Integer[] anArray) {
		Assertions.assertThat(anArray).doesNotHaveDuplicates();
		Assertions.assertThat(anArray).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Provide
	Arbitrary<Integer[]> array() {
		return Arbitraries.integers().between(0, 10).unique().array(Integer[].class).ofSize(5);
	}

	@Property
	void uniqueInStream(@ForAll @Size(5) Stream<@IntRange(min = 0, max = 10) @Unique Integer> aStream) {
		List<Integer> aList = aStream.collect(Collectors.toList());
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Property
	void uniqueIsAppliedAfterStandardConfigurators(@ForAll @Size(5) List<@Unique @IntRange(min = 0, max = 10) Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}
}
