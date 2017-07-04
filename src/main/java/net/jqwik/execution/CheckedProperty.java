package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class CheckedProperty {

	private static Supplier<Random> RNG = ThreadLocalRandom::current;

	public final String propertyName;
	public final CheckedFunction forAllPredicate;
	public final List<Parameter> forAllParameters;
	public final ArbitraryResolver arbitraryProvider;
	public final int maxDiscardRatio;
	public final int tries;
	public final long randomSeed;

	public CheckedProperty(
		String propertyName, CheckedFunction forAllPredicate, List<Parameter> forAllParameters, ArbitraryResolver arbitraryProvider, int tries,  int maxDiscardRatio, long randomSeed
	) {
		this.propertyName = propertyName;
		this.forAllPredicate = forAllPredicate;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.maxDiscardRatio = maxDiscardRatio;
		this.tries = tries;
		this.randomSeed = randomSeed;
	}

	public PropertyCheckResult check() {
		long effectiveSeed = randomSeed == Property.DEFAULT_SEED ? RNG.get().nextLong() : randomSeed;
		try {
			return createGenericProperty().check(tries, maxDiscardRatio, effectiveSeed);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(propertyName, 0, 0, effectiveSeed, Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private Arbitrary<Object> findArbitrary(Parameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryProvider.forParameter(parameter);
		return arbitraryOptional.orElseThrow(() -> new CannotFindArbitraryException(parameter));
	}

	private GenericProperty createGenericProperty() {
		List<Arbitrary> arbitraries = forAllParameters.stream().map(this::findArbitrary).collect(Collectors.toList());
		return new GenericProperty(propertyName, arbitraries, forAllPredicate);
	}

}
