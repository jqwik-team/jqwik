package net.jqwik.properties.arbitraries;

public class LongShrinkCandidates extends IntegralShrinkCandidates<Long> {

	public LongShrinkCandidates(long min, long max) {
		super(min, max);
	}

	@Override
	protected Long shrinkTowardsTarget(Long value) {
		return nextShrinkValue(value);
	}

	@Override
	public int distance(Long value) {
		return distanceFromLong(value);
	}
}
