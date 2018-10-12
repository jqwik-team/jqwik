package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.arbitraries.*;
import net.jqwik.properties.shrinking.*;

public interface ExhaustiveGenerator<T> extends Iterator<T> {

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

}
