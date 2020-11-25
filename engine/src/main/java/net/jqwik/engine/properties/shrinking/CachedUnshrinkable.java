package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class CachedUnshrinkable<T> implements Shrinkable<T> {
	private static final List<Class<?>> IMMUTABLE_CLASSES = Arrays.asList(
			String.class,
			boolean.class,
			Boolean.class,
			int.class,
			Integer.class,
			long.class,
			Long.class,
			short.class,
			Short.class,
			byte.class,
			Byte.class,
			double.class,
			Double.class,
			float.class,
			Float.class,
			BigInteger.class,
			BigDecimal.class,
			AtomicInteger.class,
			AtomicBoolean.class,
			AtomicLong.class
	);
	private final ShrinkingDistance distance;
	private final Supplier<T> valueSupplier;
	private T cachedValue = null;
	private boolean valueCached = false;

	public CachedUnshrinkable(Supplier<T> valueSupplier, ShrinkingDistance distance) {
		this.valueSupplier = valueSupplier;
		this.distance = distance;
	}

	@Override
	public T value() {
		// Caching is introduced to improve before/after reporting of falsified samples
		if (valueCached) {
			return cachedValue;
		}

		T value = valueSupplier.get();
		if (isImmutable(value)) {
			cachedValue = value;
			valueCached = true;
		}
		return value;
	}

	private boolean isImmutable(T value) {
		if (value == null) {
			return false;
		}
		return IMMUTABLE_CLASSES.contains(value.getClass());
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return Stream.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return distance;
	}

	@Override
	public String toString() {
		return JqwikStringSupport.displayString(value());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CachedUnshrinkable<?> that = (CachedUnshrinkable<?>) o;

		return Objects.equals(value(), that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value());
	}
}
