package net.jqwik.engine.properties.shrinking;

import java.util.stream.*;

import net.jqwik.api.*;

public class IgnoreExceptionShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> shrinkable;
	private final Class<? extends Throwable> exceptionType;

	public IgnoreExceptionShrinkable(Shrinkable<T> shrinkable, Class<? extends Throwable> exceptionType) {
		this.shrinkable = shrinkable;
		this.exceptionType = exceptionType;
	}

	@Override
	public T value() {
		return shrinkable.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinkable.shrink().filter(s -> {
			try {
				s.value();
				return true;
			} catch (Throwable throwable) {
				if (exceptionType.isAssignableFrom(throwable.getClass())) {
					return false;
				}
				throw throwable;
			}
		}).map(shrinkable1 -> new IgnoreExceptionShrinkable<T>(shrinkable1, exceptionType));
	}

	@Override
	public ShrinkingDistance distance() {
		return shrinkable.distance();
	}
}
