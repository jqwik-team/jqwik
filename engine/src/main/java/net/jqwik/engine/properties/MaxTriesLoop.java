package net.jqwik.engine.properties;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class MaxTriesLoop {
	private static final long MAX_MISSES = 10000;

	public static <T>  T loop(
		BooleanSupplier loopCondition,
		Function<T, Tuple2<Boolean, T>> loopAndReturn,
		Function<Long, ? extends JqwikException> tooManyMissesExceptionSupplier
	) {
		long count = 0;
		T value = null;
		while (loopCondition.getAsBoolean()) {
			Tuple2<Boolean, T> result = loopAndReturn.apply(value);
			value = result.get2();
			if (result.get1()) {
				break;
			}
			if (++count > MAX_MISSES) {
				throw tooManyMissesExceptionSupplier.apply(MAX_MISSES);
			}
		}
		return value;
	}

}
