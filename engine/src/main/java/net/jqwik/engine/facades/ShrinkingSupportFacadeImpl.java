package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

public class ShrinkingSupportFacadeImpl extends ShrinkingSupportFacade {

	private final TestingSupportFacadeImpl testingSupportFacade = new TestingSupportFacadeImpl();

	@Override
	public <T extends @Nullable Object> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<? super T> falsifier) {
		RandomGenerator<? extends T> generator = arbitrary.generator(10, true);
		return falsifyThenShrink(generator, random, falsifier);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends @Nullable Object> T falsifyThenShrink(
			RandomGenerator<? extends T> generator,
			Random random,
			Falsifier<? super T> falsifier
	) {
		Throwable[] originalError = new Throwable[1];
		Shrinkable<T> falsifiedShrinkable =
				(Shrinkable<T>) testingSupportFacade.generateUntil(generator, random, value -> {
					TryExecutionResult result = falsifier.execute(value);
					if (result.isFalsified()) {
						originalError[0] = result.throwable().orElse(null);
					}
					return result.isFalsified();
				});
		// System.out.println("### " + falsifiedShrinkable.value());
		return shrink(falsifiedShrinkable, falsifier, originalError[0]);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends @Nullable Object> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<? super T> falsifier,
			Throwable originalError
	) {
		ShrunkFalsifiedSample sample = shrinkToSample(falsifiedShrinkable, falsifier, originalError);
		return (T) sample.parameters().get(0);
	}

	@Override
	public <T extends @Nullable Object> ShrunkFalsifiedSample shrinkToSample(
		Shrinkable<T> falsifiedShrinkable,
		Falsifier<? super T> falsifier,
		Throwable originalError
	) {
		FalsifiedSample sample = toFalsifiedSample(falsifiedShrinkable, originalError);
		Consumer<FalsifiedSample> parametersReporter = ignore -> {};
		PropertyShrinker shrinker = new PropertyShrinker(sample, ShrinkingMode.FULL, 10, parametersReporter, null);

		return shrinker.shrink(toParamFalsifier(falsifier));
	}

	@SuppressWarnings("unchecked")
	private static <T extends @Nullable Object> Falsifier<List<Object>> toParamFalsifier(Falsifier<T> tFalsifier) {
		return params -> {
			// At best PropertyShrinker should be parameterized, however, currently it is not
			// Even though PropertyShrinker passes List<Object>, the elements are of type T
			T t = (T) params.get(0);
			return tFalsifier.execute(t);
		};
	}

	@SuppressWarnings("unchecked")
	private static <T> FalsifiedSample toFalsifiedSample(Shrinkable<T> falsifiedShrinkable, Throwable originalError) {
		List<Shrinkable<Object>> shrinkables = Collections.singletonList((Shrinkable<Object>) falsifiedShrinkable);
		return toFalsifiedSample(shrinkables, originalError);
	}

	private static FalsifiedSample toFalsifiedSample(List<Shrinkable<Object>> shrinkables, Throwable originalError) {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(parameters, shrinkables, Optional.ofNullable(originalError), Collections.emptyList());
	}

}
