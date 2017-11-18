package net.jqwik.execution;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collectors;

import org.junit.platform.engine.reporting.ReportEntry;

import net.jqwik.api.*;
import net.jqwik.descriptor.PropertyConfiguration;
import net.jqwik.properties.*;

public class CheckedProperty {

	private static Supplier<Random> RNG = ThreadLocalRandom::current;

	public final String propertyName;
	public final CheckedFunction forAllPredicate;
	public final List<Parameter> forAllParameters;
	public final ArbitraryResolver arbitraryProvider;
	public final PropertyConfiguration configuration;

	public CheckedProperty(String propertyName, CheckedFunction forAllPredicate, List<Parameter> forAllParameters,
			ArbitraryResolver arbitraryProvider, PropertyConfiguration configuration) {
		this.propertyName = propertyName;
		this.forAllPredicate = forAllPredicate;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.configuration = configuration;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher) {
		long effectiveSeed = configuration.getSeed() == Property.DEFAULT_SEED ? RNG.get().nextLong() : configuration.getSeed();
		PropertyConfiguration effectiveConfiguration = configuration.withSeed(effectiveSeed);
		try {
			return createGenericProperty().check(effectiveConfiguration, publisher);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(propertyName, 0, 0, effectiveConfiguration.getSeed(), Collections.emptyList(), cannotFindArbitraryException);
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
