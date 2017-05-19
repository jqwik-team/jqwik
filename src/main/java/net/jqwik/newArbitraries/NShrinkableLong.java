package net.jqwik.newArbitraries;

public class NShrinkableLong extends NShrinkableIntegral<Long> {

	public NShrinkableLong(long value, long min, long max) {
		super(value, min, max);
	}

	private NShrinkableLong(long value, long shrinkingTarget) {
		super(value, shrinkingTarget);
	}

	@Override
	public Long value() {
		return value;
	}

	protected NShrinkable<Long> shrinkTowardsTarget() {
		return new NShrinkableLong(nextShrinkValue(value, shrinkingTarget), shrinkingTarget);
	}

}
