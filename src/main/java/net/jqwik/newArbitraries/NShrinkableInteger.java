package net.jqwik.newArbitraries;

public class NShrinkableInteger extends NShrinkableIntegral<Integer> {
	public NShrinkableInteger(int value, int min, int max) {
		super(value, min, max);
	}

	private NShrinkableInteger(int value, int shrinkingTarget) {
		super(value, shrinkingTarget);
	}

	@Override
	public Integer value() {
		return Math.toIntExact(value);
	}

	protected NShrinkable<Integer> shrinkTowardsTarget() {
		return new NShrinkableInteger(Math.toIntExact(nextShrinkValue(value, shrinkingTarget)), Math.toIntExact(shrinkingTarget));
	}

}
