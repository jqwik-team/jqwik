package net.jqwik.api;

import java.util.*;
import java.util.function.*;

public class FalsificationResult<T> implements Comparable<FalsificationResult<T>> {

	private final Shrinkable<T> shrinkable;
	private final Status status;
	private final Throwable throwable;

	public enum Status {
		FALSIFIED, VERIFIED, FILTERED_OUT
	}

	public static <T> FalsificationResult<T> falsified(Shrinkable<T> shrinkable) {
		return FalsificationResult.falsified(shrinkable, null);
	}

	public static <T> FalsificationResult<T> falsified(Shrinkable<T> shrinkable, Throwable throwable) {
		return new FalsificationResult<>(shrinkable, Status.FALSIFIED, throwable);
	}


	public static <T> FalsificationResult<T> notFalsified(Shrinkable<T> shrinkable) {
		return new FalsificationResult<>(shrinkable, Status.VERIFIED, null);
	}

	public static <T> FalsificationResult<T> filtered(Shrinkable<T> shrinkable) {
		return new FalsificationResult<>(shrinkable, Status.FILTERED_OUT, null);
	}

	private FalsificationResult(Shrinkable<T> shrinkable, Status status, Throwable throwable) {
		this.shrinkable = shrinkable;
		this.status = status;
		this.throwable = throwable;
	}

	public Shrinkable<T> shrinkable() {
		return shrinkable;
	}

	public T value() {
		return shrinkable.value();
	}

	public ShrinkingDistance distance() {
		return shrinkable.distance();
	}

	public Status status() {
		return status;
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public int compareTo(FalsificationResult<T> other) {
		return shrinkable.compareTo(other.shrinkable);
	}

	public FalsificationResult<T> filter(Predicate<T> filter) {
		return new FalsificationResult<>(shrinkable.filter(filter), status, throwable);
	}

	public <U> FalsificationResult<U> map(Function<T, U> mapper) {
		return new FalsificationResult<>(shrinkable.map(mapper), status, throwable);
	}

}
