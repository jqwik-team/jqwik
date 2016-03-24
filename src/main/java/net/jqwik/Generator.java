
package net.jqwik;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Generator<T> {

	T generate();

	default List<T> generateAll() {
		return new ArrayList<T>();
	}

	List<T> shrink(T value);

	default Optional<Long> finalNumberOfValues() {
		return Optional.empty();
	}
}
