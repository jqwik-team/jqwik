package net.jqwik.execution;

import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class CheckedProperty {

	private static Supplier<Random> RNG = ThreadLocalRandom::current;

	public final String propertyName;
	public final CheckedFunction forAllFunction;
	public final List<Parameter> forAllParameters;
	public final ArbitraryProvider arbitraryProvider;
	public final int tries;
	public final long randomSeed;

	public CheckedProperty(String propertyName, CheckedFunction forAllFunction,
						   List<Parameter> forAllParameters, ArbitraryProvider arbitraryProvider, int tries, long randomSeed) {
		this.propertyName = propertyName;
		this.forAllFunction = forAllFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.tries = tries;
		this.randomSeed = randomSeed;
	}

	public PropertyCheckResult check() {
		// Long.MIN_VALUE is the default for Property.seed() annotation property
		long effectiveSeed = randomSeed == Long.MIN_VALUE ? RNG.get().nextLong() : randomSeed;
		try {
			return createGenericProperty().check(tries, effectiveSeed);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(propertyName, 0, 0, effectiveSeed, Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private Arbitrary<Object> findArbitrary(Parameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryProvider.forParameter(parameter);
		if (!arbitraryOptional.isPresent())
			throw new CannotFindArbitraryException(parameter);
		return arbitraryOptional.get();
	}

	private GenericProperty createGenericProperty() {
		List<Arbitrary> arbitraries = forAllParameters.stream().map(this::findArbitrary).collect(Collectors.toList());
		return new GenericProperty(propertyName, arbitraries, forAllFunction);
	}

	public int getTries() {
		return tries;
	}

}
