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

	//@Property //TODO: Find a way to make WithNull work for provided values
	boolean providedArbitrary(@ForAll("provided") @WithNull(1.0) String aValue) {
		return aValue == null;
	}

	@Provide
	Arbitrary<String> provided() {
		return Arbitraries.strings().map(String::toUpperCase);
	}
}
