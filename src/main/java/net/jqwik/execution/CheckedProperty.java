package net.jqwik.execution;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.*;
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
	private final Optional<ExhaustiveShrinkablesGenerator> optionalExhaustive;

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
		this.optionalExhaustive = createExhaustiveShrinkablesGenerator();
	}

	public PropertyCheckResult check(Consumer<ReportEntry> publisher, Reporting[] reporting) {
		PropertyConfiguration effectiveConfiguration = configurationWithEffectiveSeed();
		try {
			return createGenericProperty(effectiveConfiguration).check(publisher, reporting);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.erroneous(
				effectiveConfiguration.getStereotype(), propertyName, 0, 0, effectiveConfiguration.getSeed(),
				configuration.getGenerationMode(), Collections.emptyList(), null, cannotFindArbitraryException
			);
		}
	}

	private PropertyConfiguration configurationWithEffectiveSeed() {
		if (!configuration.getSeed().equals(Property.SEED_NOT_SET)) {
			return configuration.withSeed(configuration.getSeed());
		}
		if (configuration.getPreviousSeed() != null && configuration.getAfterFailureMode() != AfterFailureMode.RANDOM_SEED) {
			return configuration.withSeed(configuration.getPreviousSeed());
		}
		return configuration.withSeed(SourceOfRandomness.createRandomSeed());
	}

	private GenericProperty createGenericProperty(PropertyConfiguration configuration) {
		if (configuration.getGenerationMode() == GenerationMode.AUTO) {
			configuration = chooseGenerationMode(configuration);
		} else if (configuration.getGenerationMode() == GenerationMode.DATA_DRIVEN) {
			ensureValidDataDrivenMode();
		} else if (configuration.getGenerationMode() == GenerationMode.EXHAUSTIVE) {
			ensureValidExhaustiveMode();
			configuration = configuration.withTries(Math.toIntExact(optionalExhaustive.get().maxCount()));
		} else if (configuration.getGenerationMode() == GenerationMode.RANDOMIZED) {
			ensureValidRandomizedMode();
		}

		ShrinkablesGenerator shrinkablesGenerator = createShrinkablesGenerator(configuration);
		return new GenericProperty(propertyName, configuration, shrinkablesGenerator, checkedFunction);
	}

	private ShrinkablesGenerator createShrinkablesGenerator(PropertyConfiguration configuration) {
		if (configuration.getFalsifiedSample() != null && configuration.getAfterFailureMode() == AfterFailureMode.SAMPLE_ONLY) {
			return createSampleOnlyShrinkableGenerator(configuration);
		}
		switch (configuration.getGenerationMode()) {
			case EXHAUSTIVE:
				return optionalExhaustive.get();
			case DATA_DRIVEN:
				return createDataBasedShrinkablesGenerator(configuration);
			default:
				return createRandomizedShrinkablesGenerator(configuration);
		}
	}

	private void ensureValidRandomizedMode() {
		if (optionalData.isPresent()) {
			throw new JqwikException("You cannot have both a @FromData annotation and @Property(generation = RANDOMIZED)");
		}
	}

	private void ensureValidExhaustiveMode() {
		if (optionalData.isPresent()) {
			throw new JqwikException("You cannot have both a @FromData annotation and @Property(generation = EXHAUSTIVE)");
		}
		if (!optionalExhaustive.isPresent()) {
			throw new JqwikException("@Property(generation = EXHAUSTIVE) requires all arbitraries to provide exhaustive generators");
		}
	}

	private void ensureValidDataDrivenMode() {
		if (!optionalData.isPresent()) {
			throw new JqwikException("With @Property(generation = DATA_DRIVEN) there must be a @FromData annotation");
		}
	}

	private PropertyConfiguration chooseGenerationMode(PropertyConfiguration configuration) {
		if (optionalData.isPresent()) {
			configuration = configuration.withGenerationMode(GenerationMode.DATA_DRIVEN);
		} else if (optionalExhaustive.isPresent() && optionalExhaustive.get().maxCount() <= configuration.getTries()) {
			configuration = configuration.withGenerationMode(GenerationMode.EXHAUSTIVE);
		} else {
			configuration = configuration.withGenerationMode(GenerationMode.RANDOMIZED);
		}
		return configuration;
	}

	private Optional<ExhaustiveShrinkablesGenerator> createExhaustiveShrinkablesGenerator() {
		if (forAllParameters.isEmpty()) {
			return Optional.empty();
		}
		try {
			ExhaustiveShrinkablesGenerator exhaustiveShrinkablesGenerator =
				ExhaustiveShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver);
			return Optional.of(exhaustiveShrinkablesGenerator);
		} catch (TooManyFilterMissesException tmfme) {
			throw tmfme;
		} catch (JqwikException ex) {
			return Optional.empty();
		}
	}

	private ShrinkablesGenerator createDataBasedShrinkablesGenerator(PropertyConfiguration configuration) {
		if (configuration.getGenerationMode() != GenerationMode.DATA_DRIVEN) {
			throw new JqwikException("You cannot have both a @FromData annotation and @Property(generation = RANDOMIZED)");
		}
		return new DataBasedShrinkablesGenerator(forAllParameters, optionalData.get());
	}

	private ShrinkablesGenerator createRandomizedShrinkablesGenerator(PropertyConfiguration configuration) {
		Random random = SourceOfRandomness.create(configuration.getSeed());
		return RandomizedShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver, random, configuration.getTries());
	}

	private ShrinkablesGenerator createSampleOnlyShrinkableGenerator(PropertyConfiguration configuration) {
		return new SampleOnlyShrinkablesGenerator(forAllParameters, configuration.getFalsifiedSample());
	}


}
