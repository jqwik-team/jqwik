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

	static PropertyAttributesDefaults with(
		int tries,
		int maxDiscardRatio,
		AfterFailureMode afterFailureMode,
		GenerationMode generationMode,
		EdgeCasesMode edgeCasesMode
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
				// TODO: Fill from entry in config file
				return ShrinkingMode.BOUNDED;
			}

			@Override
			public String stereotype() {
				// TODO: Fill from entry in config file
				return DEFAULT_STEREOTYPE;
			}
		};
	}
}
