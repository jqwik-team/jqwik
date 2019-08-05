package net.jqwik.engine;

import net.jqwik.api.*;

public interface PropertyDefaultValues {
	int tries();
	int maxDiscardRatio();

	AfterFailureMode afterFailure();
	GenerationMode generation();

	static PropertyDefaultValues with(
		int tries,
		int maxDiscardRatio,
		AfterFailureMode afterFailureMode,
		GenerationMode generationMode
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
			public AfterFailureMode afterFailure() {
				return afterFailureMode;
			}

			@Override
			public GenerationMode generation() {
				return generationMode;
			}
		};
	}
}
