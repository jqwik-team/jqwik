package net.jqwik.properties.arbitraries;

public class IntegerShrinkCandidates extends IntegralShrinkCandidates<Integer> {

	public IntegerShrinkCandidates(long min, long max) {
		super(min, max);
	}

	@Override
	protected Integer shrinkTowardsTarget(Integer value) {
		return Math.toIntExact(nextShrinkValue(value));
	}

	@Override
	public int distance(Integer value) {
		return distanceFromLong(Math.toIntExact(value));
	}
}
