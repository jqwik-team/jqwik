package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class MockFalsifier<T> implements Predicate<T> {

	public static<T> MockFalsifier<T> falsifyWhen(Predicate<T> when) {
		return new MockFalsifier<T>(when);
	}

	public static<T> MockFalsifier<T> falsifyAll() {
		return falsifyWhen(v -> false);
	}

	private final List<T> visited = new ArrayList<>();
	private final Predicate<T> falsifierFunction;

	private MockFalsifier(Predicate<T> falsifierFunction) {
		this.falsifierFunction = falsifierFunction;
	}

	@Override
	public boolean test(T value) {
		visited.add(value);
		return falsifierFunction.test(value);
	}

	public List<T> visited() {
		return visited;
	}

}
