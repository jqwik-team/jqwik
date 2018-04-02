package net.jqwik;

public interface PropertyDefaultValues {
	int tries();
	int maxDiscardRatio();
	int maxShrinkingDepth();

	static PropertyDefaultValues with(int tries, int maxDiscardRatio, int maxShrinkingDepth) {
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
			public int maxShrinkingDepth() {
				return maxShrinkingDepth;
			}
		};
	}
}
