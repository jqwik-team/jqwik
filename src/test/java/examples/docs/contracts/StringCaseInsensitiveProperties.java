package examples.docs.contracts;

import java.io.*;
import java.util.*;

/**
 * Example is "borrowed" from junit-quickcheck.
 * See http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html
 */
class StringCaseInsensitiveProperties extends MyClass<Integer> implements ComparatorContract<String>, Serializable {

	@Override public Comparator<String> subject() {
		return String::compareToIgnoreCase;
	}
}

class MyClass<T> implements Iterator<T> {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}
}