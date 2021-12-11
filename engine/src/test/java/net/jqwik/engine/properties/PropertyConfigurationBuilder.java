package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.execution.*;

class PropertyConfigurationBuilder {

	static PropertyConfigurationBuilder aConfig() {
		return new PropertyConfigurationBuilder();
	}

	private String seed = null;
	private GenerationInfo generationInfo = GenerationInfo.NULL;
	private String previousSeed = null;
	private List<Object> falsifiedSample = null;
	private Integer tries = null;
	private Integer maxDiscardRatio = null;
	private ShrinkingMode shrinkingMode = null;
	private GenerationMode generationMode = null;
	private AfterFailureMode afterFailureMode = null;
	private EdgeCasesMode edgeCasesMode = null;
	private FixedSeedMode fixedSeedMode = null;

	PropertyConfigurationBuilder withSeed(String seed) {
		this.seed = seed;
		return this;
	}

	PropertyConfigurationBuilder withPreviousFailureGeneration(GenerationInfo generationInfo) {
		this.generationInfo = generationInfo;
		return this;
	}

	PropertyConfigurationBuilder withTries(int tries) {
		this.tries = tries;
		return this;
	}

	PropertyConfigurationBuilder withMaxDiscardRatio(int maxDiscardRatio) {
		this.maxDiscardRatio = maxDiscardRatio;
		return this;
	}

	PropertyConfigurationBuilder withShrinking(ShrinkingMode shrinkingMode) {
		this.shrinkingMode = shrinkingMode;
		return this;
	}

	PropertyConfigurationBuilder withGeneration(GenerationMode generationMode) {
		this.generationMode = generationMode;
		return this;
	}

	PropertyConfigurationBuilder withAfterFailure(AfterFailureMode afterFailureMode) {
		this.afterFailureMode = afterFailureMode;
		return this;
	}

	public PropertyConfigurationBuilder withEdgeCases(EdgeCasesMode edgeCasesMode) {
		this.edgeCasesMode = edgeCasesMode;
		return this;
	}

	public PropertyConfigurationBuilder withWhenFixedSeed(FixedSeedMode fixedSeedMode) {
		this.fixedSeedMode = fixedSeedMode;
		return this;
	}

	PropertyConfiguration build() {
		PropertyAttributes propertyAttributes = new DefaultPropertyAttributes(
			tries,
			maxDiscardRatio,
			shrinkingMode,
			generationMode,
			afterFailureMode,
			edgeCasesMode,
			null,
			seed,
			fixedSeedMode
		);

		return new PropertyConfiguration(
			propertyAttributes,
			TestHelper.propertyAttributesDefaults(),
			generationInfo,
			falsifiedSample,
			seed,
			tries,
			generationMode
		);

	}

}
