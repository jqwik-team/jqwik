package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(
		PropertyAttributes attributes,
		PropertyDefaultValues propertyDefaultValues,
		String previousSeed,
		List<Object> falsifiedSample
	) {
		int tries = attributes.tries().orElse(propertyDefaultValues.tries());
		int maxDiscardRatio = attributes.maxDiscardRatio().orElse(propertyDefaultValues.maxDiscardRatio());
		ShrinkingMode shrinking = attributes.shrinking().orElse(propertyDefaultValues.shrinking());
		AfterFailureMode afterFailure = attributes.afterFailure().orElse(propertyDefaultValues.afterFailure());
		GenerationMode generation = attributes.generation().orElse(propertyDefaultValues.generation());
		EdgeCasesMode edgeCasesMode = attributes.edgeCases().orElse(propertyDefaultValues.edgeCases());
		String stereotype = attributes.stereotype().orElse(propertyDefaultValues.stereotype());
		String seed = attributes.seed().orElse(Property.SEED_NOT_SET);

		return new PropertyConfiguration(
			stereotype,
			seed,
			previousSeed,
			falsifiedSample,
			tries,
			maxDiscardRatio,
			shrinking,
			generation,
			afterFailure,
			edgeCasesMode
		);
	}

	private final String stereotype;
	private final String seed;
	private final String previousSeed;
	private final List<Object> falsifiedSample;
	private final int tries;
	private final int maxDiscardRatio;
	private final ShrinkingMode shrinkingMode;
	private final GenerationMode generationMode;
	private final AfterFailureMode afterFailureMode;
	private final EdgeCasesMode edgeCasesMode;

	public PropertyConfiguration(
		String stereotype,
		String seed,
		String previousSeed,
		List<Object> falsifiedSample,
		int tries,
		int maxDiscardRatio,
		ShrinkingMode shrinkingMode,
		GenerationMode generationMode,
		AfterFailureMode afterFailureMode,
		EdgeCasesMode edgeCasesMode
	) {
		this.stereotype = stereotype;
		this.seed = seed;
		this.previousSeed = previousSeed;
		this.falsifiedSample = falsifiedSample;
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
		this.generationMode = generationMode;
		this.afterFailureMode = afterFailureMode;
		this.edgeCasesMode = edgeCasesMode;
	}

	public PropertyConfiguration withSeed(String changedSeed) {
		return new PropertyConfiguration(
			this.stereotype,
			changedSeed,
			this.previousSeed,
			this.falsifiedSample,
			this.tries,
			this.maxDiscardRatio,
			this.shrinkingMode,
			this.generationMode,
			this.afterFailureMode,
			this.edgeCasesMode
		);
	}

	public PropertyConfiguration withGenerationMode(GenerationMode changedGenerationMode) {
		return new PropertyConfiguration(
			this.stereotype,
			this.seed,
			this.previousSeed,
			this.falsifiedSample,
			this.tries,
			this.maxDiscardRatio,
			this.shrinkingMode,
			changedGenerationMode,
			this.afterFailureMode,
			this.edgeCasesMode
		);
	}

	public PropertyConfiguration withTries(int changedTries) {
		return new PropertyConfiguration(
			this.stereotype,
			this.seed,
			this.previousSeed,
			this.falsifiedSample,
			changedTries,
			this.maxDiscardRatio,
			this.shrinkingMode,
			this.generationMode,
			this.afterFailureMode,
			this.edgeCasesMode
		);
	}

	public String getSeed() {
		return seed;
	}

	public String getPreviousSeed() {
		return previousSeed;
	}

	public List<Object> getFalsifiedSample() {
		return falsifiedSample;
	}

	public String getStereotype() {
		return stereotype;
	}

	public int getTries() {
		return tries;
	}

	public int getMaxDiscardRatio() {
		return maxDiscardRatio;
	}

	public ShrinkingMode getShrinkingMode() {
		return shrinkingMode;
	}

	public GenerationMode getGenerationMode() {
		return generationMode;
	}

	public AfterFailureMode getAfterFailureMode() {
		return afterFailureMode;
	}

	public EdgeCasesMode getEdgeCasesMode() {
		return edgeCasesMode;
	}
}
