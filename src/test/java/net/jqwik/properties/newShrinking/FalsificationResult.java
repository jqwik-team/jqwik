package net.jqwik.properties.newShrinking;

import java.util.*;

public class FalsificationResult<T> implements Comparable<FalsificationResult<T>> {

	private final NShrinkable<T> shrinkable;
	private final Status status;
	private final Throwable throwable;

	public enum Status {
		FALSIFIED, VERIFIED, FILTERED_OUT
	}

	public static <T> FalsificationResult<T> falsified(NShrinkable<T> shrinkable, Throwable throwable) {
		return new FalsificationResult<>(shrinkable, Status.FALSIFIED, throwable);
	}


	public static <T> FalsificationResult<T> notFalsified(NShrinkable<T> shrinkable) {
		return new FalsificationResult<>(shrinkable, Status.VERIFIED, null);
	}

	public static <T> FalsificationResult<T> filtered(NShrinkable<T> shrinkable) {
		return new FalsificationResult<>(shrinkable, Status.FILTERED_OUT, null);
	}

	private FalsificationResult(NShrinkable<T> shrinkable, Status status, Throwable throwable) {
		this.shrinkable = shrinkable;
		this.status = status;
		this.throwable = throwable;
	}

	public NShrinkable<T> shrinkable() {
		return shrinkable;
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



}
