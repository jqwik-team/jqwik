package net.jqwik.properties;

import java.util.*;

public interface PropertyCheckResult {

	enum Status {
		SATISFIED, FALSIFIED, ERRONEOUS, EXHAUSTED
	}

	Status status();

	String propertyName();

	int tries();

	long randomSeed();

	Optional<List<Object>> sample();

	Optional<Throwable> throwable();

}
