package net.jqwik.docs.contracts;

import net.jqwik.api.*;

import java.util.*;

/**
 * Example was "borrowed" from junit-quickcheck.
 * See http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html
 */
class StringCaseInsensitiveProperties implements ComparatorContract<String> {

	@Override public Comparator<String> subject() {
		return String::compareToIgnoreCase;
	}

	@Override
	public boolean isSorted(List<String> aList) {
		if (aList.size() <= 1)
			return true;
		if (subject().compare(aList.get(0), aList.get(1)) > 0)
			return false;
		aList.remove(0);
		return isSorted(aList);
	}

	@Override
	@Provide
	public Arbitrary<String> anyT() {
		return Arbitraries.strings().alpha().ofMaxLength(20);
	}

	@Override
	@Provide
	public Arbitrary<List<String>> listOfT() {
		return anyT().list().ofMaxSize(10);
	}
}

