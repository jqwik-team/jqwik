package net.jqwik.engine.descriptor;

import java.security.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.*;

public class PropertyConfiguration {

	private static final Logger LOG = Logger.getLogger(CheckedProperty.class.getName());

	public static PropertyConfiguration from(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		GenerationInfo generationInfo
	) {
		return new PropertyConfiguration(
			propertyAttributes,
			propertyAttributesDefaults,
			generationInfo,
			null,
			null,
			null
		);
	}

	private final PropertyAttributes propertyAttributes;
	private final PropertyAttributesDefaults propertyAttributesDefaults;
	private final GenerationInfo previousFailureGeneration;
	private final String overriddenSeed;
	private final Integer overriddenTries;
	private final GenerationMode overriddenGenerationMode;

	public PropertyConfiguration(
		PropertyAttributes propertyAttributes,
		PropertyAttributesDefaults propertyAttributesDefaults,
		GenerationInfo previousFailureGeneration,
		String overriddenSeed,
		Integer overriddenTries,
		GenerationMode overriddenGenerationMode
	) {
		this.propertyAttributes = propertyAttributes;
		this.propertyAttributesDefaults = propertyAttributesDefaults;
		this.overriddenSeed = overriddenSeed;
		this.previousFailureGeneration = previousFailureGeneration;
		this.overriddenTries = overriddenTries;
		this.overriddenGenerationMode = overriddenGenerationMode;
	}

	public PropertyConfiguration withSeed(String changedSeed) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousFailureGeneration,
			changedSeed,
			this.overriddenTries,
			this.overriddenGenerationMode
		);
	}

	public PropertyConfiguration withGenerationMode(GenerationMode changedGenerationMode) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousFailureGeneration,
			this.overriddenSeed,
			this.overriddenTries,
			changedGenerationMode
		);
	}

	public PropertyConfiguration withTries(int changedTries) {
		return new PropertyConfiguration(
			this.propertyAttributes,
			this.propertyAttributesDefaults,
			this.previousFailureGeneration,
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
		return propertyAttributes.seed().orElse(propertyAttributesDefaults.seed());
	}

	public GenerationMode getGenerationMode() {
		if (overriddenGenerationMode != null) {
			return overriddenGenerationMode;
		}
		return propertyAttributes.generation().orElse(propertyAttributesDefaults.generation());
	}

	public GenerationInfo getPreviousFailureGeneration() {
		return previousFailureGeneration;
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

	public boolean hasFixedSeed() {
		return !getSeed().equals(Property.SEED_NOT_SET);
	}

	public boolean previousFailureMustBeHandled() {
		return previousRunFailed() && this.getAfterFailureMode() != AfterFailureMode.RANDOM_SEED;
	}

	private boolean previousRunFailed() {
		return previousFailureGeneration.randomSeed().isPresent();
	}

	public PropertyConfiguration withPreviousGenerationSeed() {
		return previousFailureGeneration.randomSeed().map(this::withSeed).orElse(this);
	}

	public PropertyConfiguration withFixedSeed(CheckedProperty checkedProperty) {
		if (getSeed().equals(Property.SEED_FROM_NAME))
			return withSeed(getSeedFromPropertyName(checkedProperty.propertyName));
		else
			return withSeed(getSeed());
	}

	private String getSeedFromPropertyName(String propertyName) {
		try {
			byte[] bytes = MessageDigest.getInstance("MD5")
										.digest(propertyName.getBytes());
			long seed = 0;
			for (int i = 0; i < 8; i++) {
				seed += (seed << 8) + (bytes[i] & 0xff);
			}
			LOG.info("calculated seed for " + propertyName + ": " + seed);
			return Long.toString(seed);
		} catch (NoSuchAlgorithmException e) {
			throw new JqwikException("", e);
		}
	}

	public boolean seedHasNotChanged() {
		return getSeed().equals(getPreviousFailureGeneration().randomSeed().orElse(null));
	}
}
