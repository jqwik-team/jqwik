package net.jqwik.execution;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;

public class CheckedProperty {

	public final String propertyName;
	public final CheckedFunction checkedFunction;
	public final List<MethodParameter> forAllParameters;
	private final ArbitraryResolver arbitraryResolver;
	private final Optional<Iterable<? extends Tuple>> optionalData;
	public final PropertyConfiguration configuration;

	public CheckedProperty(
		String propertyName, CheckedFunction checkedFunction, List<MethodParameter> forAllParameters,
		ArbitraryResolver arbitraryResolver, Optional<Iterable<? extends Tuple>> optionalData, PropertyConfiguration configuration
	) {
		this.propertyName = propertyName;
		this.checkedFunction = checkedFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryResolver = arbitraryResolver;
		this.optionalData = optionalData;
		this.configuration = configuration;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher, Reporting[] reporting) {
		PropertyConfiguration effectiveConfiguration = configurationWithEffectiveSeed();
		try {
			return createGenericProperty(effectiveConfiguration).check(publisher, reporting);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(effectiveConfiguration.getStereotype(), propertyName, 0, 0, effectiveConfiguration.getSeed(), Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private PropertyConfiguration configurationWithEffectiveSeed() {
		String effectiveSeed = configuration.getSeed().equals(Property.SEED_NOT_SET)
								   ? SourceOfRandomness.createRandomSeed()
								   : configuration.getSeed();
		return configuration.withSeed(effectiveSeed);
	}

	private GenericProperty createGenericProperty(PropertyConfiguration configuration) {
		ShrinkablesGenerator shrinkablesGenerator =
			optionalData.isPresent()
				? createDataBasedShrinkablesGenerator()
				: createRandomizedShrinkablesGenerator(configuration);
		return new GenericProperty(propertyName, configuration, shrinkablesGenerator, checkedFunction);
	}

	private ShrinkablesGenerator createDataBasedShrinkablesGenerator() {
		return new DataBasedShrinkablesGenerator(forAllParameters, optionalData.get());
	}

	private ShrinkablesGenerator createRandomizedShrinkablesGenerator(PropertyConfiguration configuration) {
		Random random = SourceOfRandomness.create(configuration.getSeed());
		return RandomizedShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver, random, configuration.getTries());
	}

}
