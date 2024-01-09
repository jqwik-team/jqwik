package experiments;

import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class Experiments {

	@Example
	void integers() {
		IntegerArbitrary integers = Arbitraries.integers().withDistribution(RandomDistribution.uniform());

		integers.sampleStream().limit(50).forEach(System.out::println);
	}

	@Provide
	Arbitrary<Tuple.Tuple2<String, Supplier<ExecutorService>>> services() {
		return Arbitraries.of(
			Tuple.of("newSingleThreadExecutor", () -> Executors.newSingleThreadExecutor()),
			Tuple.of("newFixedThreadPool", () -> Executors.newFixedThreadPool(2)),
			Tuple.of("newCachedThreadPool", () -> Executors.newCachedThreadPool()),
			Tuple.of("newVirtualThreadPerTaskExecutor", () -> Executors.newWorkStealingPool())
		);
	}

	@Property
	void test(@ForAll("services") Tuple.Tuple2<String, Supplier<ExecutorService>> pair) throws Exception {
		String name = pair.get1();
		// String service = pair.get2().get().toString();
		System.out.println("Name: " + name);
		// System.out.println("Service: " + service);
	}

}

