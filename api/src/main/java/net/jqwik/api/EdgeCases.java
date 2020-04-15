package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface EdgeCases<T> extends Iterable<Shrinkable<T>> {

	@API(status = INTERNAL)
	static <T> EdgeCases<T> none() {
		return Collections::emptyIterator;
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromStream(Stream<Shrinkable<T>> streamOfEdgeCaseShrinkables) {
		return streamOfEdgeCaseShrinkables::iterator;
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSet(Set<Shrinkable<T>> setOfEdgeCaseShrinkables) {
		return setOfEdgeCaseShrinkables::iterator;
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		return () -> {
			Stream<Shrinkable<T>> stream = StreamSupport.stream(EdgeCases.this.spliterator(), false);
			return stream.map(shrinkable -> shrinkable.map(mapper)).iterator();
		};
	}
}
