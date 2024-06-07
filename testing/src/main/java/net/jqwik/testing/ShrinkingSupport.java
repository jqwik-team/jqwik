package net.jqwik.testing;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;
import net.jqwik.api.lifecycle.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;
import static org.assertj.core.api.Assertions.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class ShrinkingSupport {

	private ShrinkingSupport() {
	}

	public static <T extends @Nullable Object> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random) {
		return falsifyThenShrink(arbitrary, random, ignore -> TryExecutionResult.falsified(null));
	}

	public static <T extends @Nullable Object> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, Random random, Falsifier<? super T> falsifier) {
		return ShrinkingSupportFacade.implementation.falsifyThenShrink(arbitrary, random, falsifier);
	}

	public static <T extends @Nullable Object> T falsifyThenShrink(RandomGenerator<? extends T> generator, Random random, Falsifier<? super T> falsifier) {
		return ShrinkingSupportFacade.implementation.falsifyThenShrink(generator, random, falsifier);
	}

	public static <T extends @Nullable Object> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<? super T> falsifier,
			Throwable originalError
	) {
		return ShrinkingSupportFacade.implementation.shrink(falsifiedShrinkable, falsifier, originalError);
	}

	public static <T extends @Nullable Object> ShrunkFalsifiedSample shrinkToSample(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<? super T> falsifier,
			Throwable originalError
	) {
		return ShrinkingSupportFacade.implementation.shrinkToSample(falsifiedShrinkable, falsifier, originalError);
	}

	public static <T extends @Nullable Object> void assertWhileShrinking(Shrinkable<T> shrinkable, Predicate<? super T> condition) {
		while(true) {
			List<Shrinkable<T>> collect = shrinkable.shrink().collect(Collectors.toList());
			assertThat(collect).allMatch(s -> condition.test(s.value()));
			if (collect.isEmpty()) {
				break;
			}
			shrinkable = collect.iterator().next();
		}
	}

	public static <T extends @Nullable Object> void assertAllValuesAreShrunkTo(Arbitrary<T> arbitrary, Random random, T expectedShrunkValue) {
		T value = falsifyThenShrink(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(expectedShrunkValue);
	}


}
