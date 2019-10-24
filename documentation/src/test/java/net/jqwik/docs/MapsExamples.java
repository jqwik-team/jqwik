package net.jqwik.docs;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class MapsExamples {


	@Property
	void defaultMapsUseDefaultTypes(@ForAll @Size(min = 1) Map<Integer, String> map) {
		Assertions.assertThat(map).isNotEmpty();
		Assertions.assertThat(map.keySet()).allMatch(key -> key instanceof Integer);
		Assertions.assertThat(map.values()).allMatch(value -> value instanceof String);
	}

	@Property
	void mapsFromNumberToNumberString(@ForAll("numberMaps")  Map<Integer, String> map) {
		Assertions.assertThat(map.keySet()).allMatch(key -> key >= 0 && key <= 1000);
		Assertions.assertThat(map.values()).allMatch(value -> value.length() == 5);
	}

	@Provide
	Arbitrary<Map<Integer, String>> numberMaps() {
		Arbitrary<Integer> keys = Arbitraries.integers().between(1, 100);
		Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);
		return Arbitraries.maps(keys, values);
	}

}
