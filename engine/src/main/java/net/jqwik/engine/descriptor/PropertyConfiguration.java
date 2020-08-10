package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		String previousSeed,
		List<Object> falsifiedSample
	) {
		return new PropertyConfiguration(
			propertyAttributes,
			propertyAttributesDefaults,
			previousSeed,
			falsifiedSample,
			null,
			null,
			null
		);
	}

	private final PropertyAttributes propertyAttributes;
	private final PropertyAttributesDefaults propertyAttributesDefaults;
	private final String previousSeed;
	private final List<Object> falsifiedSample;
	private final String overriddenSeed;
	private final Integer overriddenTries;
	private final GenerationMode overriddenGenerationMode;

	public PropertyConfiguration(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		String previousSeed, List<Object> falsifiedSample, String overriddenSeed,
		Integer overriddenTries,
		GenerationMode overriddenGenerationMode
	) {
		this.propertyAttributes = propertyAttributes;
		this.propertyAttributesDefaults = propertyAttributesDefaults;
		this.overriddenSeed = overriddenSeed;
		this.previousSeed = previousSeed;
		this.falsifiedSample = falsifiedSample;
		this.overriddenTries = overriddenTries;
		this.overriddenGenerationMode = overriddenGenerationMode;
	}

	public PropertyConfiguration withSeed(String changedSeed) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousSeed, this.falsifiedSample, changedSeed,
			this.overriddenTries,
			this.overriddenGenerationMode
		);
	}

	public PropertyConfiguration withGenerationMode(GenerationMode changedGenerationMode) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousSeed, this.falsifiedSample, this.overriddenSeed,
			this.overriddenTries,
			changedGenerationMode
		);
	}

	public PropertyConfiguration withTries(int changedTries) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousSeed, this.falsifiedSample, this.overriddenSeed,
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

	public String getPreviousSeed() {
		return previousSeed;
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
}
