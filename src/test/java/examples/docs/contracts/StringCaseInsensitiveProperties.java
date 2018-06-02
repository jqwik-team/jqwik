package examples.docs.contracts;

import java.util.*;

/**
 * Example is "borrowed" from junit-quickcheck.
 * See http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html
 */
class StringCaseInsensitiveProperties implements ComparatorContract<String> {

	@Override public Comparator<String> subject() {
		return String::compareToIgnoreCase;
	}
}