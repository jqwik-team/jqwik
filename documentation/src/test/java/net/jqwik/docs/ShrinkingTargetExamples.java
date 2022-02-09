package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.Statistics;

class ShrinkingTargetExamples {

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	boolean shrinkTo10SignalsWithFrequency50(@ForAll("signals") List<Signal> signalStream) {
		Statistics.collect(signalStream.size());
		return signalStream.size() < 10;
	}

	@Provide
	Arbitrary<List<? extends Signal>> signals() {
		Arbitrary<Long> frequencies =
			Arbitraries
				.longs()
				.between(45, 55)
				.shrinkTowards(50L);

		return frequencies.map(f -> Signal.withFrequency(f)).list().ofMaxSize(1000);
	}

	static class Signal {

		private Long frequency;

		public Signal(Long frequency) {
			this.frequency = frequency;
		}

		public static Signal withFrequency(Long frequency) {
			return new Signal(frequency);
		}

		@Override
		public String toString() {
			return String.format("Signal(%s)", frequency);
		}
	}
}
