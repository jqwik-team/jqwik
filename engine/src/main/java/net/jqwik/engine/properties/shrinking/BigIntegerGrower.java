package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.stream.*;

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
		BigInteger diff = calculateDiff(beforeValue, afterValue, value);
		if (diff.compareTo(BigInteger.ZERO) != 0) {
			BigInteger grownValue = value.add(diff);
			if (sameSign(shrinkingTarget.subtract(value), shrinkingTarget.subtract(grownValue)) && range.includes(grownValue)) {
				return Optional.of(new ShrinkableBigInteger(grownValue, range, shrinkingTarget));
			}
		}
		return Optional.empty();
	}

	private BigInteger calculateDiff(Object beforeValue, Object afterValue, BigInteger current) {
		BigInteger before;
		BigInteger after;
		if (beforeValue instanceof BigInteger && afterValue instanceof BigInteger) {
			before = (BigInteger) beforeValue;
			after = (BigInteger) afterValue;
		} else {
			before = BigInteger.valueOf(toLong(beforeValue));
			after = BigInteger.valueOf(toLong(afterValue));
		}
		if (sameSign(before, current)) {
			return before.subtract(after);
		} else {
			return after.subtract(before);
		}
	}

	private boolean sameSign(BigInteger first, BigInteger second) {
		return Math.abs(first.signum() - second.signum()) <= 1;
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

	public Stream<Shrinkable<BigInteger>> grow(BigInteger value, Range<BigInteger> range, BigInteger shrinkingTarget) {
		if (value.compareTo(shrinkingTarget) < 0) {
			return growLeft(value, range, shrinkingTarget);
		} else {
			return growRight(value, range, shrinkingTarget);
		}
	}

	private Stream<Shrinkable<BigInteger>> growRight(BigInteger value, Range<BigInteger> range, BigInteger shrinkingTarget) {
		return Stream
				   .of(
					   range.max,
					   value.add(range.max.subtract(value).divide(BigInteger.valueOf(2))),
					   value.add(BigInteger.TEN),
					   value.add(BigInteger.ONE)
				   )
				   .filter(grownValue -> grownValue.compareTo(value) > 0)
				   .filter(range::includes)
				   .distinct()
				   .map(grown -> new ShrinkableBigInteger(grown, range, shrinkingTarget));
	}

	private Stream<Shrinkable<BigInteger>> growLeft(BigInteger value, Range<BigInteger> range, BigInteger shrinkingTarget) {
		return Stream
				   .of(
					   range.min,
					   value.subtract(value.subtract(range.min).divide(BigInteger.valueOf(2))),
					   value.subtract(BigInteger.TEN),
					   value.subtract(BigInteger.ONE)
				   )
				   .filter(grownValue -> grownValue.compareTo(value) < 0)
				   .filter(range::includes)
				   .distinct()
				   .map(grown -> new ShrinkableBigInteger(grown, range, shrinkingTarget));
	}
}
