package net.jqwik.properties.arbitraries;

public class ShortShrinkCandidates extends IntegralShrinkCandidates<Short> {

	public ShortShrinkCandidates(short min, short max) {
		super(min, max);
	}

	@Override
	protected Short shrinkTowardsTarget(Short value) {
		return (short) nextShrinkValue(value);
	}

	@Override
	protected Short shrinkOneTowardsTarget(Short value) {
		return (short) nextShrinkOne(value);
	}

	@Override
	public int distance(Short value) {
		return distanceFromLong(Math.toIntExact(value));
	}
}
