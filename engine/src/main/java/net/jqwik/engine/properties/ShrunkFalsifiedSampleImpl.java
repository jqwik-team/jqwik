package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class ShrunkFalsifiedSampleImpl extends FalsifiedSampleImpl implements ShrunkFalsifiedSample {

	private final int shrinkingSteps;

	public ShrunkFalsifiedSampleImpl(
		FalsifiedSample falsifiedSample,
		int shrinkingSteps
	) {
		this(falsifiedSample.parameters(), falsifiedSample.shrinkables(), falsifiedSample.falsifyingError(), shrinkingSteps);
	}

	public ShrunkFalsifiedSampleImpl(
		List<Object> parameters,
		List<Shrinkable<Object>> shrinkables,
		Optional<Throwable> falsifyingError,
		int shrinkingSteps
	) {
		super(parameters, shrinkables, falsifyingError);
		this.shrinkingSteps = shrinkingSteps;
	}

	@Override
	public int countShrinkingSteps() {
		return shrinkingSteps;
	}

	public boolean equivalentTo(FalsifiedSample sample) {
		if (sample == null) {
			return false;
		}
		ShrunkFalsifiedSample shrunkWithZeroSteps =
			new ShrunkFalsifiedSampleImpl(sample.parameters(), sample.shrinkables(), sample.falsifyingError(), 0);
		return this.equals(shrunkWithZeroSteps);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ShrunkFalsifiedSample)) return false;
		if (!super.equals(o)) return false;
		ShrunkFalsifiedSample that = (ShrunkFalsifiedSample) o;
		return shrinkingSteps == that.countShrinkingSteps();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), shrinkingSteps);
	}
}
