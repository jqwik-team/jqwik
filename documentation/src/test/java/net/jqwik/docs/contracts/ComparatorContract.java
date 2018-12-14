package net.jqwik.docs.contracts;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;

import static java.lang.Integer.signum;

/**
 * Example was "borrowed" from junit-quickcheck.
 * See http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html
 */
interface ComparatorContract<T> {
	Comparator<T> subject();

	@Property @Report(Reporting.GENERATED)
	default void symmetry(@ForAll("anyT") T x, @ForAll("anyT") T y) {
		Comparator<T> subject = subject();

		Assertions.assertThat(signum(subject.compare(x, y))).isEqualTo(-signum(subject.compare(y, x)));
	}

	@Property @Report(Reporting.GENERATED)
	default boolean sorting(@ForAll("listOfT") List<T> aList) {
		aList.sort(subject());
		return isSorted(aList);
	}

	boolean isSorted(List<T> aList);

	@Provide
	Arbitrary<T> anyT();

	@Provide
	Arbitrary<List<T>> listOfT();
}