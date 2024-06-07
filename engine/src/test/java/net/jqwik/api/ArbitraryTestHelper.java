package net.jqwik.api;

import java.util.function.*;

import net.jqwik.engine.*;
import net.jqwik.testing.*;

import org.jspecify.annotations.*;

public class ArbitraryTestHelper {

	public static <T extends @Nullable Object> void assertAllGenerated(RandomGenerator<? extends T> generator, Consumer<? super T> assertions) {
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
