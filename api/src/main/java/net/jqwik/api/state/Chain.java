package net.jqwik.api.state;

import org.apiguardian.api.*;

import java.util.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Chain<T> extends Iterable<T> {

	Iterator<T> start();

	@Override
	default Iterator<T> iterator() {
		return start();
	}

	List<Mutator<T>> appliedMutators();

	int countIterations();

	int maxSize();
}
