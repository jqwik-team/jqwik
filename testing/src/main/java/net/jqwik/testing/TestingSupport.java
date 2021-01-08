package net.jqwik.testing;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.assertj.core.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class TestingSupport {

	private TestingSupport() {
	}

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Random random, Predicate<T> checker) {
		Optional<? extends Shrinkable<? extends T>> failure =
				generator
						.stream(random)
						.limit(100)
						.filter(shrinkable -> !checker.test(shrinkable.value()))
						.findAny();

		failure.ifPresent(shrinkable -> {
			Assertions.fail(String.format("Value [%s] failed to fulfill condition.", shrinkable.value().toString()));
		});
	}

	public static <T> Set<T> collectEdgeCases(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}

}
