package net.jqwik.api.constraints;

import java.util.*;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

class AnnotatedParameterTypesProperties {

	@Property
	boolean withNullInParameterType(@ForAll List<@WithNull(1.0) String> aValue) {
		return aValue.stream().allMatch(Objects::isNull);
	}

	@Property(tries = 20)
	void fixedSizedListWithFixedLengthString(
		@ForAll @Size(3) List<@StringLength(5) @Chars({'a', 'b', 'c'}) String> aStringList
	) {
		Assertions.assertThat(aStringList).hasSize(3);
		Assertions.assertThat(aStringList.stream()).allMatch(s -> s.length() == 5);
		Assertions.assertThat(aStringList.stream()).allMatch(s -> {
			for (char c : s.toCharArray()) {
				if (! "abc".contains(String.valueOf(c)))
					return false;
			}
			return true;
		});
	}

}
