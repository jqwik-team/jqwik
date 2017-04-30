package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class FalsifiedShrinker {

	private final List<Arbitrary> arbitraries;
	private final Function<List<Object>, Boolean> forAllFunction;

	public FalsifiedShrinker(List<Arbitrary> arbitraries, Function<List<Object>, Boolean> forAllFunction) {
		this.arbitraries = arbitraries;
		this.forAllFunction = forAllFunction;
	}

	// TODO: Really shrink the params
	public List<Object> shrink(List<Object> originalParams) {
		return originalParams;
	}
}
