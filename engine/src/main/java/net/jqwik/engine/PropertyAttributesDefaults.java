package net.jqwik.engine;

import net.jqwik.api.*;

public interface PropertyAttributesDefaults {

	String DEFAULT_STEREOTYPE = "Property";

	int tries();
	int maxDiscardRatio();

	ShrinkingMode shrinking();
	AfterFailureMode afterFailure();
	GenerationMode generation();
	EdgeCasesMode edgeCases();
	String stereotype();
	FixedSeedMode whenFixedSeed();

	public String seed();

	// This is currently a global parameter
	int boundedShrinkingSeconds();

	static PropertyAttributesDefaults with(
		int tries,
		int maxDiscardRatio,
		AfterFailureMode afterFailureMode,
		GenerationMode generationMode,
		EdgeCasesMode edgeCasesMode,
		ShrinkingMode shrinkingMode,
		int boundedShrinkingSeconds,
		FixedSeedMode fixedSeedMode,
		String seed
	) {
		return new PropertyAttributesDefaults() {
			@Override
			public int tries() {
				return tries;
			}

			@Override
			public int maxDiscardRatio() {
				return maxDiscardRatio;
			}

			@Override
			public AfterFailureMode afterFailure() {
				return afterFailureMode;
			}

			@Override
			public GenerationMode generation() {
				return generationMode;
			}

			@Override
			public EdgeCasesMode edgeCases() {
				return edgeCasesMode;
			}

			@Override
			public ShrinkingMode shrinking() {
				return shrinkingMode;
			}

			@Override
			public String stereotype() {
				return DEFAULT_STEREOTYPE;
			}

			@Override
			public int boundedShrinkingSeconds() {
				return boundedShrinkingSeconds;
			}

			@Override
			public FixedSeedMode whenFixedSeed() {
				return fixedSeedMode;
			}

			@Override
			public String seed() {return seed;}
		};
	}
}
