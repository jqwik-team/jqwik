package net.jqwik.api.facades;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public abstract class ShrinkingSupportFacade {
	public static final ShrinkingSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(ShrinkingSupportFacade.class);
	}

	public abstract <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, JqwikRandom random, Falsifier<T> falsifier);

	public abstract <T> T falsifyThenShrink(RandomGenerator<? extends T> arbitrary, JqwikRandom random, Falsifier<T> falsifier);

	public abstract <T> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	);

	public abstract <T> ShrunkFalsifiedSample shrinkToSample(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	);
}
