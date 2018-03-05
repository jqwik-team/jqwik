package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class CheckedProperty {

	public final String propertyName;
	public final CheckedFunction forAllPredicate;
	public final List<MethodParameter> forAllParameters;
	public final ArbitraryResolver arbitraryProvider;
	public final PropertyConfiguration configuration;

	public CheckedProperty(String propertyName, CheckedFunction forAllPredicate, List<MethodParameter> forAllParameters,
			ArbitraryResolver arbitraryProvider, PropertyConfiguration configuration) {
		this.propertyName = propertyName;
		this.forAllPredicate = forAllPredicate;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.configuration = configuration;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher) {
		String effectiveSeed = configuration.getSeed()
											.equals(Property.SEED_NOT_SET) ? SourceOfRandomness.createRandomSeed() : configuration
			.getSeed();
		PropertyConfiguration effectiveConfiguration = configuration.withSeed(effectiveSeed);
		try {
			return createGenericProperty().check(effectiveConfiguration, publisher);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(effectiveConfiguration.getStereotype(), propertyName, 0, 0, effectiveConfiguration.getSeed(), Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private Arbitrary<Object> findArbitrary(MethodParameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryProvider.forParameter(parameter);
		return arbitraryOptional.orElseThrow(() -> new CannotFindArbitraryException(parameter));
	}

	private GenericProperty createGenericProperty() {
		List<Arbitrary> arbitraries = forAllParameters.stream().map(this::findArbitrary).collect(Collectors.toList());
		return new GenericProperty(propertyName, arbitraries, forAllPredicate);
	}

}
