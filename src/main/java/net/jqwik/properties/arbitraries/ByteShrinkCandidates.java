package net.jqwik.properties.arbitraries;

public class ByteShrinkCandidates extends IntegralShrinkCandidates<Byte> {

	public ByteShrinkCandidates(long min, long max) {
		super(min, max);
	}

	@Override
	protected Byte shrinkTowardsTarget(Byte value) {
		return (byte) nextShrinkValue(value);
	}

	@Override
	protected Byte shrinkOneTowardsTarget(Byte value) {
		return (byte) nextShrinkOne(value);
	}

	@Override
	public int distance(Byte value) {
		return distanceFromLong(Math.toIntExact(value));
	}
}
