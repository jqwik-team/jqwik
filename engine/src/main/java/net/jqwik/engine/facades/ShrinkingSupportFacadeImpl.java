package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class ShrinkingSupportFacadeImpl extends ShrinkingSupportFacade {

	private final TestingSupportFacadeImpl testingSupportFacade = new TestingSupportFacadeImpl();

	@Override
	public <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, JqwikRandom random, Falsifier<T> falsifier) {
		RandomGenerator<? extends T> generator = arbitrary.generator(10, true);
		return falsifyThenShrink(generator, random, falsifier);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T falsifyThenShrink(
		RandomGenerator<? extends T> generator,
		JqwikRandom random,
		Falsifier<T> falsifier
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
	public <T> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	) {
		ShrunkFalsifiedSample sample = shrinkToSample(falsifiedShrinkable, falsifier, originalError);
		return (T) sample.parameters().get(0);
	}

	@Override
	public <T> ShrunkFalsifiedSample shrinkToSample(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	) {
		FalsifiedSample sample = toFalsifiedSample(falsifiedShrinkable, originalError);
		Consumer<FalsifiedSample> parametersReporter = ignore -> {};
		PropertyShrinker shrinker = new PropertyShrinker(sample, ShrinkingMode.FULL, 10, parametersReporter, null);

		return shrinker.shrink(toParamFalsifier(falsifier));
	}

	@SuppressWarnings("unchecked")
	private static <T> Falsifier<List<Object>> toParamFalsifier(Falsifier<T> tFalsifier) {
		return params -> {
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
