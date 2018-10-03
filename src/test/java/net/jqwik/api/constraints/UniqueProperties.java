package net.jqwik.api.constraints;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class UniqueProperties {

	@Property
	void uniqueInList(@ForAll @Size(5) List<@IntRange(min = 0, max = 10) @Unique Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}

	@Property
	void uniqueIsAppliedAfterStandardConfigurators(@ForAll @Size(5) List<@Unique @IntRange(min = 0, max = 10) Integer> aList) {
		Assertions.assertThat(aList).doesNotHaveDuplicates();
		Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
	}
}
