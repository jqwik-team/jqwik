package net.jqwik;

import net.jqwik.api.*;

public interface PropertyDefaultValues {
	int tries();
	int maxDiscardRatio();

	AfterFailureMode afterFailure();

	static PropertyDefaultValues with(
		int tries,
		int maxDiscardRatio,
		AfterFailureMode afterFailureMode
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
		};
	}
}
