package net.jqwik.api;

import java.util.function.*;

import net.jqwik.engine.*;
import net.jqwik.testing.*;

public class ArbitraryTestHelper {

	public static <T> void assertAllGenerated(RandomGenerator<? extends T> generator, Consumer<T> assertions) {
		Predicate<T> checker = value -> {
			try {
				assertions.accept(value);
				return true;
			} catch (Throwable any) {
				return false;
			}
		};
		TestingSupport.checkAllGenerated(generator, SourceOfRandomness.current(), checker);
	}

}
