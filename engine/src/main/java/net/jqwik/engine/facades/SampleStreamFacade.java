package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jspecify.annotations.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.engine.TestDescriptor.Type.*;

class SampleStreamFacade {

	private static final TestDescriptor SAMPLE_STREAM_DESCRIPTOR = new AbstractTestDescriptor(
		UniqueId.root("jqwik", "samples"),
		"Streaming samples outside jqwik thread"
	) {
		@Override
		public Type getType() {
			return TEST;
		}
	};

	private static final Map<Arbitrary<Object>, RandomGenerator<Object>> generators = new LruCache<>(500);

	@SuppressWarnings("unchecked")
	private static <T extends @Nullable Object> RandomGenerator<T> getGeneratorForSampling(Arbitrary<T> arbitrary) {
		return runInDescriptor(() -> getGenerator((Arbitrary<Object>) arbitrary));
	}

	@SuppressWarnings("unchecked")
	private static <T> RandomGenerator<T> getGenerator(Arbitrary<Object> arbitrary) {
		RandomGenerator<Object> generator = generators.get(arbitrary);
		if (generator == null) {
			generator = arbitrary.generator(JqwikProperties.DEFAULT_TRIES, true);
			generators.put(arbitrary, generator);
		}
		return (RandomGenerator<T>) generator;

		// Using computeIfAbsent will throw CurrentModificationException when getting
		// the generator from the arbitrary will itself add another generator
		// to the map of generators:
		// return (RandomGenerator<T>) generators.computeIfAbsent(
		// 		arbitrary,
		// 		a -> a.generator(JqwikProperties.DEFAULT_TRIES, true)
		// );
	}

	private static <T extends @Nullable Object> Supplier<T> wrapInDescriptor(Supplier<T> code) {
		return () -> runInDescriptor(code);
	}

	private static <T extends @Nullable Object> T runInDescriptor(Supplier<T> code) {
		if (CurrentTestDescriptor.isEmpty()) {
			return CurrentTestDescriptor.runWithDescriptor(SAMPLE_STREAM_DESCRIPTOR, code);
		} else {
			return code.get();
		}
	}

	<T extends @Nullable Object> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		RandomGenerator<T> generator = getGeneratorForSampling(arbitrary);
		return Stream.generate(wrapInDescriptor(() -> generator.next(SourceOfRandomness.current())))
					 .map(shrinkable -> runInDescriptor(shrinkable::value));
	}
}
