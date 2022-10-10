package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

public class DefaultListCombinator<T> implements Combinators.ListCombinator<T> {

	private final Arbitrary<T>[] arbitraries;

	public DefaultListCombinator(List<Arbitrary<T>> listOfArbitraries) {
		arbitraries = listOfArbitraries.toArray(new Arbitrary[listOfArbitraries.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Arbitrary<R> as(Function<List<T>, @NotNull R> combinator) {
		final Function<List<Object>, R> combineFunction = params -> combinator.apply((List<T>) params);
		return new CombineArbitrary<>(combineFunction, arbitraries);
	}

	@API(status = EXPERIMENTAL, since = "1.7.1")
	public Combinators.ListCombinator<T> filter(Predicate<List<T>> filter) {
		return new FilteredListCombinator<>(arbitraries, filter);
	}

}
