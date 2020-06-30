package net.jqwik.engine.execution;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class CheckedProperty {

	public final String propertyName;
	public final TryLifecycleExecutor tryLifecycleExecutor;
	public final List<MethodParameter> propertyParameters;
	public final List<MethodParameter> forAllParameters;
	public final PropertyConfiguration configuration;

	private final ArbitraryResolver arbitraryResolver;
	private final ResolveParameterHook resolveParameterHook;
	private final PropertyLifecycleContext propertyLifecycleContext;
	private final Optional<Iterable<? extends Tuple>> optionalData;
	private Optional<ExhaustiveShrinkablesGenerator> optionalExhaustive;

	public CheckedProperty(
		String propertyName,
		TryLifecycleExecutor tryLifecycleExecutor,
		List<MethodParameter> propertyParameters,
		ArbitraryResolver arbitraryResolver,
		ResolveParameterHook resolveParameterHook,
		PropertyLifecycleContext propertyLifecycleContext,
		Optional<Iterable<? extends Tuple>> optionalData,
		PropertyConfiguration configuration
	) {
		this.propertyName = propertyName;
		this.tryLifecycleExecutor = tryLifecycleExecutor;
		this.propertyParameters = propertyParameters;
		this.forAllParameters = selectForAllParameters(propertyParameters);
		this.arbitraryResolver = arbitraryResolver;
		this.resolveParameterHook = resolveParameterHook;
		this.propertyLifecycleContext = propertyLifecycleContext;
		this.optionalData = optionalData;
		this.configuration = configuration;
	}

	private List<MethodParameter> selectForAllParameters(List<MethodParameter> propertyParameters) {
		return propertyParameters.stream().filter(parameter -> parameter.isAnnotated(ForAll.class)).collect(Collectors.toList());
	}

	public PropertyCheckResult check(Reporting[] reporting) {
		PropertyConfiguration effectiveConfiguration = configurationWithEffectiveSeed();
		try {
			Reporter reporter = propertyLifecycleContext.reporter();
			return createGenericProperty(effectiveConfiguration).check(reporter, reporting);
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyCheckResult.failed(
				effectiveConfiguration.getStereotype(), propertyName, 0, 0,
				effectiveConfiguration.getSeed(), configuration.getGenerationMode(),
				configuration.getEdgeCasesMode(), 0, 0,
				null, null, cannotFindArbitraryException
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

		if (configuration.getGenerationMode() == GenerationMode.RANDOMIZED) {
			ensureValidRandomizedMode();
		} else if (configuration.getGenerationMode() == GenerationMode.DATA_DRIVEN) {
			ensureValidDataDrivenMode();
		} else if (configuration.getGenerationMode() == GenerationMode.EXHAUSTIVE) {
			ensureValidExhaustiveMode();
			configuration = configuration.withTries(
				Math.toIntExact(getOptionalExhaustive().get().maxCount())
			);
		} else if (configuration.getGenerationMode() == GenerationMode.AUTO) {
			configuration = chooseGenerationMode(configuration);
		}
		ForAllParametersGenerator shrinkablesGenerator = createShrinkablesGenerator(configuration);
		ResolvingParametersGenerator parametersGenerator = new ResolvingParametersGenerator(
			propertyParameters,
			shrinkablesGenerator,
			resolveParameterHook,
			propertyLifecycleContext
		);
		Supplier<TryLifecycleContext> tryLifecycleContextSupplier = () -> new DefaultTryLifecycleContext(propertyLifecycleContext, resolveParameterHook);
		return new GenericProperty(propertyName, configuration, parametersGenerator, tryLifecycleExecutor, tryLifecycleContextSupplier);
	}

	private ForAllParametersGenerator createShrinkablesGenerator(PropertyConfiguration configuration) {
		List<Object> falsifiedSample = configuration.getFalsifiedSample();
		if (falsifiedSample != null && !falsifiedSample.isEmpty()) {
			if (configuration.getAfterFailureMode() == AfterFailureMode.SAMPLE_ONLY) {
				return createSampleOnlyShrinkableGenerator(configuration);
			} else if (configuration.getAfterFailureMode() == AfterFailureMode.SAMPLE_FIRST) {
				return createSampleOnlyShrinkableGenerator(configuration)
						   .andThen(() -> createDefaultShrinkablesGenerator(configuration));
			}
		}
		return createDefaultShrinkablesGenerator(configuration);
	}

	private ForAllParametersGenerator createDefaultShrinkablesGenerator(PropertyConfiguration configuration) {
		switch (configuration.getGenerationMode()) {
			case EXHAUSTIVE:
				return getOptionalExhaustive().get();
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
		if (!getOptionalExhaustive().isPresent()) {
			throw new JqwikException("EXHAUSTIVE generation is not possible. Maybe too many potential examples?");
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
		} else if (getOptionalExhaustive().isPresent() && getOptionalExhaustive().get().maxCount() <= configuration.getTries()) {
			configuration = configuration.withGenerationMode(GenerationMode.EXHAUSTIVE);
		} else {
			configuration = configuration.withGenerationMode(GenerationMode.RANDOMIZED);
		}
		return configuration;
	}

	private Optional<ExhaustiveShrinkablesGenerator> createOptionalExhaustiveShrinkablesGenerator(long maxNumberOfSamples) {
		if (forAllParameters.isEmpty()) {
			return Optional.empty();
		}
		try {
			ExhaustiveShrinkablesGenerator exhaustiveShrinkablesGenerator =
				ExhaustiveShrinkablesGenerator.forParameters(forAllParameters, arbitraryResolver, maxNumberOfSamples);
			return Optional.of(exhaustiveShrinkablesGenerator);
		} catch (TooManyFilterMissesException tmfme) {
			throw tmfme;
		} catch (JqwikException ex) {
			return Optional.empty();
		}
	}

	private ForAllParametersGenerator createDataBasedShrinkablesGenerator(PropertyConfiguration configuration) {
		if (configuration.getGenerationMode() != GenerationMode.DATA_DRIVEN) {
			throw new JqwikException("You cannot have both a @FromData annotation and @Property(generation = RANDOMIZED)");
		}
		return new DataBasedShrinkablesGenerator(forAllParameters, optionalData.get());
	}

	private ForAllParametersGenerator createRandomizedShrinkablesGenerator(PropertyConfiguration configuration) {
		Random random = SourceOfRandomness.create(configuration.getSeed());
		return RandomizedShrinkablesGenerator.forParameters(
			forAllParameters,
			arbitraryResolver,
			random,
			configuration.getTries(),
			configuration.getEdgeCasesMode()
		);
	}

	private ForAllParametersGenerator createSampleOnlyShrinkableGenerator(PropertyConfiguration configuration) {
		return new SampleOnlyShrinkablesGenerator(forAllParameters, configuration.getFalsifiedSample());
	}

	private Optional<ExhaustiveShrinkablesGenerator> getOptionalExhaustive() {
		//noinspection OptionalAssignedToNull
		if (optionalExhaustive == null) {
			long maxNumberOfSamples = configuration.getGenerationMode() == GenerationMode.EXHAUSTIVE
										  ? ExhaustiveGenerator.MAXIMUM_SAMPLES_TO_GENERATE : configuration.getTries();
			optionalExhaustive = createOptionalExhaustiveShrinkablesGenerator(maxNumberOfSamples);
		}
		return optionalExhaustive;
	}
}
