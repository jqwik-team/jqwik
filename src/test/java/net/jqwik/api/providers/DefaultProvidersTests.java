package net.jqwik.api.providers;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class DefaultProvidersTests {

	private enum AnEnum {
		One,
		Two,
		Three
	}

	@Property
	boolean intParam(@ForAll int aValue) {
		return true;
	}

	@Property
	boolean integerParam(@ForAll Integer aValue) {
		return aValue != null;
	}

	@Property
	boolean longParam(@ForAll long aValue) {
		return true;
	}

	@Property
	boolean longerParam(@ForAll Long aValue) {
		return aValue != null;
	}

	@Property
	boolean doubleParam(@ForAll double aValue) {
		return true;
	}

	@Property
	boolean doublerParam(@ForAll Double aValue) {
		return aValue != null;
	}

	@Property
	boolean floatParam(@ForAll float aValue) {
		return true;
	}

	@Property
	boolean floaterParam(@ForAll Float aValue) {
		return aValue != null;
	}

	@Property
	boolean booleanParam(@ForAll boolean aValue) {
		return true;
	}

	@Property
	boolean boxedBooleanParam(@ForAll Boolean aValue) {
		return aValue != null;
	}

	@Property
	boolean charParam(@ForAll char aValue) {
		return true;
	}

	@Property
	boolean boxedCharacterParam(@ForAll Character aValue) {
		return aValue != null;
	}

	@Property
	boolean enumParam(@ForAll AnEnum aValue) {
		return true;
	}

	@Property
	boolean bigIntegerParam(@ForAll BigInteger aValue) {
		return aValue != null;
	}

	@Property
	boolean bigDecimalParam(@ForAll BigDecimal aValue) {
		return aValue != null;
	}

	@Property
	boolean stringParam(@ForAll String aValue) {
		return aValue != null;
	}

	@Property
	boolean integerList(@ForAll List<Integer> aValue) {
		return aValue != null;
	}

	@Property
	boolean integerArray(@ForAll Integer[] aValue) {
		return aValue != null;
	}

	@Property
	boolean integerSet(@ForAll Set<Integer> aValue) {
		return aValue != null;
	}

	@Property
	boolean integerStream(@ForAll Stream<Integer> aValue) {
		return aValue != null;
	}

	@Property
	boolean integerOptional(@ForAll Optional<Integer> aValue) {
		return aValue != null;
	}

}
