package net.jqwik.api.facades;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public abstract class ShrinkingSupportFacade {
	public static final ShrinkingSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(ShrinkingSupportFacade.class);
	}

	public abstract <T extends @Nullable Object> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<? super T> falsifier);

	public abstract <T extends @Nullable Object> T falsifyThenShrink(RandomGenerator<? extends T> arbitrary, Random random, Falsifier<? super T> falsifier);

	public abstract <T extends @Nullable Object> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<? super T> falsifier,
			Throwable originalError
	);

	public abstract <T extends @Nullable Object> ShrunkFalsifiedSample shrinkToSample(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<? super T> falsifier,
			Throwable originalError
	);
}
