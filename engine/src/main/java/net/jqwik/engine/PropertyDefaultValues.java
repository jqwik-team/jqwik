package net.jqwik.engine;

import net.jqwik.api.*;

public interface PropertyDefaultValues {
	int tries();
	int maxDiscardRatio();

	ShrinkingMode shrinking();
	AfterFailureMode afterFailure();
	GenerationMode generation();
	EdgeCasesMode edgeCases();

	static PropertyDefaultValues with(
		int tries,
		int maxDiscardRatio,
		AfterFailureMode afterFailureMode,
		GenerationMode generationMode,
		EdgeCasesMode edgeCasesMode
	) {
		return new PropertyDefaultValues() {
			@Override
			public int tries() {
				return tries;
			}

			@Override
			public int maxDiscardRatio() {
				return maxDiscardRatio;
			}

			@Override
			public ShrinkingMode shrinking() {
				// TODO: Fill from entry in config file
				return ShrinkingMode.BOUNDED;
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
		};
	}
}
