package net.jqwik.api.constraints;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

import org.jspecify.annotations.*;

@StatisticsReport(onFailureOnly = true)
class WithNullProperties {

	@Property
	boolean strings(@ForAll @WithNull(1.0) @Nullable String aValue) {
		return aValue == null;
	}

	@Property
	void defaultProbability(@ForAll @WithNull @Nullable String stringOrNull) {
		Statistics.label("is null")
				  .collect(stringOrNull == null)
				  .coverage(coverage -> coverage.check(true).percentage(p -> p >= 1 && p <= 10));
	}

	@Property
	boolean lists(@ForAll @WithNull(1.0) @Nullable List<String> aValue) {
		return aValue == null;
	}

	@Property
	boolean sets(@ForAll @WithNull(1.0) @Nullable Set<String> aValue) {
		return aValue == null;
	}

	@Property
	boolean stream(@ForAll @WithNull(1.0) @Nullable Stream<String> aValue) {
		return aValue == null;
	}

	@Property
	boolean arrays(@ForAll @WithNull(1.0) @Nullable String[] aValue) {
		return aValue == null;
	}

	@Property
	boolean providedArbitrary(@ForAll("provided") @WithNull(1.0) @Nullable String aValue) {
		return aValue == null;
	}

	@Provide
	Arbitrary<String> provided() {
		return Arbitraries.strings().map(String::toUpperCase);
	}
}
