package net.jqwik.engine.properties;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class MaxTriesLoop {

	public static <T>  T loop(
		BooleanSupplier loopCondition,
		Function<T, Tuple2<Boolean, T>> loopAndReturn,
		Function<Integer, ? extends JqwikException> tooManyMissesExceptionSupplier,
		int maxMisses
	) {
		long count = 0;
		T value = null;
		while (loopCondition.getAsBoolean()) {
			Tuple2<Boolean, T> result = loopAndReturn.apply(value);
			value = result.get2();
			if (result.get1()) {
				break;
			}
			if (++count > maxMisses) {
				throw tooManyMissesExceptionSupplier.apply(maxMisses);
			}
		}
		return value;
	}

}
