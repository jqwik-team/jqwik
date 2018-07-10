package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.function.*;

public class CheckedProperty {

	public final String propertyName;
	public final CheckedFunction forAllPredicate;
	public final List<MethodParameter> forAllParameters;
	public final ArbitraryResolver arbitraryResolver;
	public final PropertyConfiguration configuration;

	public CheckedProperty(String propertyName, CheckedFunction forAllPredicate, List<MethodParameter> forAllParameters,
						   ArbitraryResolver arbitraryResolver, PropertyConfiguration configuration) {
		this.propertyName = propertyName;
		this.forAllPredicate = forAllPredicate;
		this.forAllParameters = forAllParameters;
		this.arbitraryResolver = arbitraryResolver;
		this.configuration = configuration;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher) {
		String effectiveSeed = configuration.getSeed()
											.equals(Property.SEED_NOT_SET) ? SourceOfRandomness.createRandomSeed() : configuration
			.getSeed();
		PropertyConfiguration effectiveConfiguration = configuration.withSeed(effectiveSeed);
		try {
			return createGenericProperty(effectiveConfiguration.getTries()).check(effectiveConfiguration, publisher);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(effectiveConfiguration.getStereotype(), propertyName, 0, 0, effectiveConfiguration.getSeed(), Collections.emptyList(), cannotFindArbitraryException);
		}
	}

	private GenericProperty createGenericProperty(int genSize) {
		ShrinkablesGenerator shrinkablesGenerator = PropertyMethodShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver, genSize);
		return new GenericProperty(propertyName, shrinkablesGenerator, forAllPredicate);
	}

}
