package net.jqwik.api.state;

import java.util.*;

public interface ChangeDetector<T> {

	static <T> ChangeDetector<T> forImmutables() {
		return new ChangeDetector<T>() {
			private T before = null;

			@Override
			public void before(T before) {
				this.before = before;
			}

			@Override
			public boolean hasChanged(T after) {
				return !Objects.equals(before, after);
			}
		};
	}

	static <T> ChangeDetector<T> alwaysTrue() {
		return new ChangeDetector<T>() {
			@Override
			public void before(T before) {
			}

			@Override
			public boolean hasChanged(T after) {
				return true;
			}
		};
	}

	void before(T before);

	boolean hasChanged(T after);
}
