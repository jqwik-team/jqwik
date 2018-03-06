package net.jqwik.api.constraints;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class WithNullProperties {

	@Property
	boolean strings(@ForAll @WithNull(1.0) String aValue) {
		return aValue == null;
	}

	@Property
	boolean lists(@ForAll @WithNull(1.0) List<String> aValue) {
		return aValue == null;
	}

	//@Property TODO: Enable as soon as annotations in parameter types are used
	boolean parameterValue(@ForAll List<@WithNull(1.0) String> aValue) {
		return aValue.stream().allMatch(Objects::isNull);
	}

	@Property
	boolean sets(@ForAll @WithNull(1.0) Set<String> aValue) {
		return aValue == null;
	}

	@Property
	boolean stream(@ForAll @WithNull(1.0) Stream<String> aValue) {
		return aValue == null;
	}

	@Property
	boolean arrays(@ForAll @WithNull(1.0) String[] aValue) {
		return aValue == null;
	}

	@Property
	boolean providedArbitrary(@ForAll("provided") @WithNull(1.0) String aValue) {
		return aValue == null;
	}

	@Provide
	Arbitrary<String> provided() {
		return Arbitraries.strings().map(String::toUpperCase);
	}
}
