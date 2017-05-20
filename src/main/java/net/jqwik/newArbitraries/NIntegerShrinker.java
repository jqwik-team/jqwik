package net.jqwik.newArbitraries;

public class NIntegerShrinker extends NIntegralShrinker<Integer> {

	protected NIntegerShrinker(long min, long max) {
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
