package net.jqwik.engine.properties.shrinking;

import java.util.stream.*;

import net.jqwik.api.*;

import static net.jqwik.engine.support.JqwikExceptionSupport.*;

public class IgnoreExceptionShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> shrinkable;
	private final Class<? extends Throwable>[] exceptionTypes;

	public IgnoreExceptionShrinkable(Shrinkable<T> shrinkable, Class<? extends Throwable>[] exceptionTypes) {
		this.shrinkable = shrinkable;
		this.exceptionTypes = exceptionTypes;
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
				if (isInstanceOfAny(throwable, exceptionTypes)) {
					return false;
				}
				throw throwable;
			}
		}).map(shrinkable1 -> new IgnoreExceptionShrinkable<T>(shrinkable1, exceptionTypes));
	}

	@Override
	public ShrinkingDistance distance() {
		return shrinkable.distance();
	}
}
