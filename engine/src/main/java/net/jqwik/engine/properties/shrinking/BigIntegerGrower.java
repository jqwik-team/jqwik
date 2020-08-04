package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class BigIntegerGrower {

	Optional<Shrinkable<BigInteger>> grow(
		BigInteger value,
		Range<BigInteger> range,
		BigInteger shrinkingTarget,
		Shrinkable<?> before,
		Shrinkable<?> after
	) {
		Object beforeValue = before.value();
		Object afterValue = after.value();
		BigInteger diff = calculateDiff(beforeValue, afterValue);
		if (diff.compareTo(BigInteger.ZERO) != 0) {
			BigInteger grownValue = value.add(diff);
			if (range.includes(grownValue)) {
				return Optional.of(new ShrinkableBigInteger(grownValue, range, shrinkingTarget));
			}
		}
		return Optional.empty();
	}

	private BigInteger calculateDiff(Object beforeValue, Object afterValue) {
		if (beforeValue instanceof BigInteger && afterValue instanceof BigInteger) {
			return ((BigInteger) beforeValue).subtract((BigInteger) afterValue);
		}
		return BigInteger.valueOf(toLong(beforeValue) - toLong(afterValue));
	}

	private long toLong(Object value) {
		return tryNumberTypeCasts(value, Integer.class, Long.class, Short.class, Byte.class);
	}

	private long tryNumberTypeCasts(Object value, Class<?>... targetClasses) {
		return tryNumberTypeCasts(value, new ArrayList<>(Arrays.asList(targetClasses)));
	}

	private long tryNumberTypeCasts(Object value, List<Class<?>> targetClasses) {
		if (targetClasses.isEmpty()) {
			return 0L;
		}
		try {
			Class<?> targetClass = targetClasses.remove(0);
			return ((Number) targetClass.cast(value)).longValue();
		} catch (Throwable cannotCastToTargetClass) {
			return tryNumberTypeCasts(value, targetClasses);
		}
	}

}
