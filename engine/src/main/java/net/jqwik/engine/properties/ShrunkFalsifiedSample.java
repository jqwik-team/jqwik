package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

public class ShrunkFalsifiedSample extends FalsifiedSample {

	private final int shrinkingSteps;

	public ShrunkFalsifiedSample(
		FalsifiedSample falsifiedSample,
		int shrinkingSteps
	) {
		this(falsifiedSample.parameters(), falsifiedSample.shrinkables(), falsifiedSample.falsifyingError(), shrinkingSteps);
	}
	public ShrunkFalsifiedSample(
		List<Object> parameters,
		List<Shrinkable<Object>> shrinkables,
		Optional<Throwable> falsifyingError,
		int shrinkingSteps
	) {
		super(parameters, shrinkables, falsifyingError);
		this.shrinkingSteps = shrinkingSteps;
	}

	public int countShrinkingSteps() {
		return shrinkingSteps;
	}

	public boolean equivalentTo(FalsifiedSample sample) {
		if (sample == null) {
			return false;
		}
		ShrunkFalsifiedSample shrunkWithZeroSteps =
			new ShrunkFalsifiedSample(sample.parameters(), sample.shrinkables(), sample.falsifyingError(), 0);
		return this.equals(shrunkWithZeroSteps);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ShrunkFalsifiedSample that = (ShrunkFalsifiedSample) o;
		return shrinkingSteps == that.shrinkingSteps;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), shrinkingSteps);
	}
}
