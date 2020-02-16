package net.jqwik.engine.execution;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;

import static org.assertj.core.api.Assertions.*;

class StoreTests {

	@Example
	void createAndGetLocalStore() {
		Store.create("counter", Lifespan.PROPERTY, () -> 42);
		Store<Integer> counter = Store.get("counter");
		assertThat(counter.get()).isEqualTo(42);
	}

	@Example
	void cannotCreateSameStoreTwice() {
		Store.create("counter", Lifespan.PROPERTY, () -> 42);
		assertThatThrownBy(() -> Store.create("counter", Lifespan.PROPERTY, () -> 42))
			.isInstanceOf(JqwikException.class);
	}

	@Property(tries = 10)
	@PerProperty(AssertCounter110.class)
	void getOrCreateAStore() {
		Store<Integer> counter = Store.getOrCreate("counter", Lifespan.PROPERTY, () -> 100);
		counter.update(i -> i + 1);
	}

	class AssertCounter110 implements PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			Store<Integer> counter = Store.get("counter");
			assertThat(counter.get()).isEqualTo(110);
		}
	}

	@Property(tries = 10)
	void getOrCreateWithDifferentLifespanFails() {
		Store.getOrCreate("counter", Lifespan.PROPERTY, () -> 100);
		assertThatThrownBy(() -> Store.getOrCreate("counter", Lifespan.TRY, () -> 100))
			.isInstanceOf(JqwikException.class);
	}

	@Group
	@Label("Lifespan.RUN")
	class LifespanRun implements AutoCloseable {
		Store<Integer> lifespanRun = Store.getOrCreate("run", Lifespan.RUN, () -> 0);

		@Example
		void run1() {
			lifespanRun.update(i -> i + 1);
		}

		@Example
		void run2() {
			lifespanRun.update(i -> i + 1);
		}

		@Override
		public void close() {
			assertThat(lifespanRun.get()).isIn(1, 2);
		}
	}

	@Group
	@Label("Lifespan.PROPERTY")
	class LifespanProperty implements AutoCloseable {
		Store<Integer> lifespanProperty = Store.getOrCreate("run", Lifespan.PROPERTY, () -> 0);

		@Property(tries = 10)
		void run1() {
			lifespanProperty.update(i -> i + 1);
		}

		@Property(tries = 10)
		void run2() {
			lifespanProperty.update(i -> i + 1);
		}

		@Override
		public void close() {
			assertThat(lifespanProperty.get()).isEqualTo(10);
		}
	}

	@Group
	@Label("Lifespan.TRY")
	class LifespanTry implements AutoCloseable {
		Store<Integer> lifespanTry = Store.getOrCreate("run", Lifespan.TRY, () -> 0);

		@Property(tries = 10)
		void run1() {
			lifespanTry.update(i -> 42);
			assertThat(lifespanTry.get()).isEqualTo(42);
		}

		@Property(tries = 10)
		void run2() {
			lifespanTry.update(i -> 43);
			assertThat(lifespanTry.get()).isEqualTo(43);
		}

		@Override
		public void close() {
			assertThat(lifespanTry.get()).isEqualTo(0);
		}
	}
}
