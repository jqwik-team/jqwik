package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(
		Property property,
		PropertyDefaultValues propertyDefaultValues,
		String previousSeed,
		List<Object> falsifiedSample
	) {
		int tries = property.tries() == Property.TRIES_NOT_SET
						? propertyDefaultValues.tries()
						: property.tries();

		int maxDiscardRatio = property.maxDiscardRatio() == Property.MAX_DISCARD_RATIO_NOT_SET
								  ? propertyDefaultValues.maxDiscardRatio()
								  : property.maxDiscardRatio();

		AfterFailureMode afterFailure = property.afterFailure() == AfterFailureMode.NOT_SET
											? propertyDefaultValues.afterFailure()
											: property.afterFailure();

		GenerationMode generation = property.generation() == GenerationMode.NOT_SET
										? propertyDefaultValues.generation()
										: property.generation();

		EdgeCasesMode edgeCasesMode = property.edgeCases() == EdgeCasesMode.NOT_SET
			? propertyDefaultValues.edgeCases()
			: property.edgeCases();

		return new PropertyConfiguration(
			property.stereotype(),
			property.seed(),
			previousSeed,
			falsifiedSample,
			tries,
			maxDiscardRatio,
			property.shrinking(),
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
