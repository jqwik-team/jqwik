package net.jqwik;

public interface PropertyDefaultValues {
	int tries();
	int maxDiscardRatio();

	static PropertyDefaultValues with(int tries, int maxDiscardRatio) {
		return new PropertyDefaultValues() {
			@Override
			public int tries() {
				return tries;
			}

			@Override
			public int maxDiscardRatio() {
				return maxDiscardRatio;
			}
		};
	}
}
