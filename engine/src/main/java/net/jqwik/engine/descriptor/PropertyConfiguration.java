package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		GenerationInfo generationInfo,
		List<Object> falsifiedSample
	) {
		return new PropertyConfiguration(
			propertyAttributes,
			propertyAttributesDefaults,
			generationInfo,
			falsifiedSample,
			null,
			null,
			null
		);
	}

	private final PropertyAttributes propertyAttributes;
	private final PropertyAttributesDefaults propertyAttributesDefaults;
	private final GenerationInfo generationInfo;
	private final List<Object> falsifiedSample;
	private final String overriddenSeed;
	private final Integer overriddenTries;
	private final GenerationMode overriddenGenerationMode;

	public PropertyConfiguration(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		GenerationInfo generationInfo,
		List<Object> falsifiedSample,
		String overriddenSeed,
		Integer overriddenTries,
		GenerationMode overriddenGenerationMode
	) {
		this.propertyAttributes = propertyAttributes;
		this.propertyAttributesDefaults = propertyAttributesDefaults;
		this.overriddenSeed = overriddenSeed;
		this.generationInfo = generationInfo;
		this.falsifiedSample = falsifiedSample;
		this.overriddenTries = overriddenTries;
		this.overriddenGenerationMode = overriddenGenerationMode;
	}

	public PropertyConfiguration withSeed(String changedSeed) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.generationInfo,
			this.falsifiedSample,
			changedSeed,
			this.overriddenTries,
			this.overriddenGenerationMode
		);
	}

	public PropertyConfiguration withGenerationMode(GenerationMode changedGenerationMode) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.generationInfo,
			this.falsifiedSample,
			this.overriddenSeed,
			this.overriddenTries,
			changedGenerationMode
		);
	}

	public PropertyConfiguration withTries(int changedTries) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.generationInfo,
			this.falsifiedSample,
			this.overriddenSeed,
			changedTries,
			this.overriddenGenerationMode
		);
	}

	public PropertyAttributes getPropertyAttributes() {
		return propertyAttributes;
	}

	public int getTries() {
		if (overriddenTries != null) {
			return overriddenTries;
		}
		return propertyAttributes.tries().orElse(propertyAttributesDefaults.tries());
	}

	public String getSeed() {
		if (overriddenSeed != null) {
			return overriddenSeed;
		}
		return propertyAttributes.seed().orElse(Property.SEED_NOT_SET);
	}

	public GenerationMode getGenerationMode() {
		if (overriddenGenerationMode != null) {
			return overriddenGenerationMode;
		}
		return propertyAttributes.generation().orElse(propertyAttributesDefaults.generation());
	}

	public GenerationInfo getPreviousGeneration() {
		return generationInfo;
	}

	public List<Object> getFalsifiedSample() {
		return falsifiedSample;
	}

	public String getStereotype() {
		return propertyAttributes.stereotype().orElse(propertyAttributesDefaults.stereotype());
	}

	public int getMaxDiscardRatio() {
		return propertyAttributes.maxDiscardRatio().orElse(propertyAttributesDefaults.maxDiscardRatio());
	}

	public ShrinkingMode getShrinkingMode() {
		return propertyAttributes.shrinking().orElse(propertyAttributesDefaults.shrinking());
	}

	public AfterFailureMode getAfterFailureMode() {
		return propertyAttributes.afterFailure().orElse(propertyAttributesDefaults.afterFailure());
	}

	public EdgeCasesMode getEdgeCasesMode() {
		return propertyAttributes.edgeCases().orElse(propertyAttributesDefaults.edgeCases());
	}

	// This is currently a global value and not property specific
	public int boundedShrinkingSeconds() {
		return propertyAttributesDefaults.boundedShrinkingSeconds();
	}

	public FixedSeedMode getFixedSeedMode() {
		return propertyAttributes.whenFixedSeed().orElse(propertyAttributesDefaults.whenFixedSeed());
	}
}
