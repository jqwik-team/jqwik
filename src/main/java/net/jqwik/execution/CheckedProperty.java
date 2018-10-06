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
	public final ArbitraryResolver arbitraryResolver;
	public final DataResolver dataResolver;
	public final PropertyConfiguration configuration;

	public CheckedProperty(
		String propertyName, CheckedFunction checkedFunction, List<MethodParameter> forAllParameters,
		ArbitraryResolver arbitraryResolver, DataResolver dataResolver, PropertyConfiguration configuration
	) {
		this.propertyName = propertyName;
		this.checkedFunction = checkedFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryResolver = arbitraryResolver;
		this.dataResolver = dataResolver;
		this.configuration = configuration;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher) {
		PropertyConfiguration effectiveConfiguration = configurationWithEffectiveSeed();
		try {
			return createGenericProperty(effectiveConfiguration).check(publisher);
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
		// TODO: When data is available -> createDataDrivenShrinkablesGenerator
		ShrinkablesGenerator shrinkablesGenerator = createRandomizedShrinkablesGenerator(configuration);
		return new GenericProperty(propertyName, configuration, shrinkablesGenerator, checkedFunction);
	}

	private ShrinkablesGenerator createRandomizedShrinkablesGenerator(PropertyConfiguration configuration) {
		Random random = SourceOfRandomness.create(configuration.getSeed());
		return RandomizedShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver, random, configuration.getTries());
	}

}
