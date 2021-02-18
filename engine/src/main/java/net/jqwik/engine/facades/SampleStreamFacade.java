package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.lifecycle.*;

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

	private static final Map<Arbitrary<Object>, RandomGenerator<Object>> generators = new HashMap<>();

	@SuppressWarnings("unchecked")
	private static <T> RandomGenerator<T> getGeneratorForSampling(Arbitrary<T> arbitrary) {
		return runInDescriptor(() -> getGenerator((Arbitrary<Object>) arbitrary));
	}

	@SuppressWarnings("unchecked")
	private static <T> RandomGenerator<T> getGenerator(Arbitrary<Object> arbitrary) {
		return (RandomGenerator<T>) generators.computeIfAbsent(
				arbitrary,
				a -> a.generator(JqwikProperties.DEFAULT_TRIES, true)
		);
	}

	private static <T> Supplier<T> wrapInDescriptor(Supplier<T> code) {
		return () -> CurrentTestDescriptor.runWithDescriptor(SAMPLE_STREAM_DESCRIPTOR, code);
	}

	private static <T> T runInDescriptor(Supplier<T> code) {
		if (CurrentTestDescriptor.isEmpty()) {
			return CurrentTestDescriptor.runWithDescriptor(SAMPLE_STREAM_DESCRIPTOR, code);
		} else {
			return code.get();
		}
	}

	<T> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		RandomGenerator<T> generator = getGeneratorForSampling(arbitrary);
		return Stream.generate(wrapInDescriptor(() -> generator.next(SourceOfRandomness.current())))
					 .map(shrinkable -> runInDescriptor(() -> shrinkable.value()));
	}

}
