package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.support.*;

public class ShrunkFalsifiedSampleImpl extends FalsifiedSampleImpl implements ShrunkFalsifiedSample {

	private final int shrinkingSteps;

	public ShrunkFalsifiedSampleImpl(
		FalsifiedSample falsifiedSample,
		int shrinkingSteps
	) {
		this(falsifiedSample.parameters(), falsifiedSample.shrinkables(), falsifiedSample.falsifyingError(), shrinkingSteps, falsifiedSample.footnotes());
	}

	public ShrunkFalsifiedSampleImpl(
		List<Object> parameters,
		List<Shrinkable<Object>> shrinkables,
		Optional<Throwable> falsifyingError,
		int shrinkingSteps,
		List<String> footnotes
	) {
		super(parameters, shrinkables, falsifyingError, footnotes);
		this.shrinkingSteps = shrinkingSteps;
	}

	@Override
	public int countShrinkingSteps() {
		return shrinkingSteps;
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
		return HashCodeSupport.hash(super.hashCode(), shrinkingSteps);
	}
}
