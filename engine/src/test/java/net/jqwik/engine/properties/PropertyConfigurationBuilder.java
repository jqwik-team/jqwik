package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.descriptor.*;

class PropertyConfigurationBuilder {

	static PropertyConfigurationBuilder aConfig() {
		return new PropertyConfigurationBuilder();
	}

	private String seed = "1000";
	private String previousSeed = null;
	private List<Object> falsifiedSample = null;
	private int tries = 100;
	private int maxDiscardRatio = 5;
	private ShrinkingMode shrinkingMode = ShrinkingMode.FULL;
	private GenerationMode generationMode = GenerationMode.AUTO;
	private AfterFailureMode afterFailureMode = AfterFailureMode.PREVIOUS_SEED;
	private EdgeCasesMode edgeCasesMode = EdgeCasesMode.MIXIN;


	PropertyConfigurationBuilder withSeed(String seed) {
		this.seed = seed;
		return this;
	}

	PropertyConfigurationBuilder withPreviousSeed(String seed) {
		this.previousSeed = seed;
		return this;
	}

	PropertyConfigurationBuilder withFalsifiedSample(List<Object> sample) {
		this.falsifiedSample = sample;
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


	PropertyConfiguration build() {
		return new PropertyConfiguration(
			"Property",
			seed,
			previousSeed,
			falsifiedSample,
			tries,
			maxDiscardRatio,
			shrinkingMode,
			generationMode,
			afterFailureMode,
			edgeCasesMode
		);

	}

}
