package net.jqwik.testing;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.facades.*;
import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;
import static org.assertj.core.api.Assertions.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class ShrinkingSupport {

	private ShrinkingSupport() {
	}

	public static <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, JqwikRandom random) {
		return falsifyThenShrink(arbitrary, random, ignore -> TryExecutionResult.falsified(null));
	}

	public static <T> T falsifyThenShrink(Arbitrary<? extends T> arbitrary, JqwikRandom random, Falsifier<T> falsifier) {
		return ShrinkingSupportFacade.implementation.falsifyThenShrink(arbitrary, random, falsifier);
	}

	public static <T> T falsifyThenShrink(RandomGenerator<? extends T> generator, JqwikRandom random, Falsifier<T> falsifier) {
		return ShrinkingSupportFacade.implementation.falsifyThenShrink(generator, random, falsifier);
	}

	public static <T> T shrink(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	) {
		return ShrinkingSupportFacade.implementation.shrink(falsifiedShrinkable, falsifier, originalError);
	}

	public static <T> ShrunkFalsifiedSample shrinkToSample(
			Shrinkable<T> falsifiedShrinkable,
			Falsifier<T> falsifier,
			Throwable originalError
	) {
		return ShrinkingSupportFacade.implementation.shrinkToSample(falsifiedShrinkable, falsifier, originalError);
	}

	public static <T> void assertWhileShrinking(Shrinkable<T> shrinkable, Predicate<T> condition) {
		while(true) {
			List<Shrinkable<T>> collect = shrinkable.shrink().collect(Collectors.toList());
			assertThat(collect).allMatch(s -> condition.test(s.value()));
			if (collect.isEmpty()) {
				break;
			}
			shrinkable = collect.iterator().next();
		}
	}

}
