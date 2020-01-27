package net.jqwik.engine.execution;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.Store.*;

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
	void getOrCreateAStore() {
		Store<Integer> counter = Store.getOrCreate("counter", Lifespan.PROPERTY, () -> 100);
		counter.update(i -> i + 1);
		PropertyLifecycle.onSuccess(() -> {
			assertThat(counter.get()).isEqualTo(110);
		});
	}

	@Property(tries = 10)
	void getOrCreateWithDifferentLifespanFails() {
		Store.getOrCreate("counter", Lifespan.PROPERTY, () -> 100);
		assertThatThrownBy(() -> Store.getOrCreate("counter", Lifespan.TRY, () -> 100))
			.isInstanceOf(JqwikException.class);
	}

	@Group
	@Label("Lifespan.RUN")
	class LifespanRun {
		Store<Integer> lifespanRun = Store.getOrCreate("run", Lifespan.RUN, () -> 0);

		@Example
		void run1() {
			lifespanRun.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanRun.get()).isEqualTo(1);
			});
		}

		@Example
		void run2() {
			lifespanRun.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanRun.get()).isEqualTo(2);
			});
		}
	}

	@Group
	@Label("Lifespan.PROPERTY")
	class LifespanProperty {
		Store<Integer> lifespanProperty = Store.getOrCreate("run", Lifespan.PROPERTY, () -> 0);

		@Property(tries = 10)
		void run1() {
			lifespanProperty.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanProperty.get()).isEqualTo(10);
			});
		}

		@Property(tries = 10)
		void run2() {
			lifespanProperty.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanProperty.get()).isEqualTo(10);
			});
		}
	}

	@Group
	@Label("Lifespan.TRY")
	class LifespanTry {
		Store<Integer> lifespanTry = Store.getOrCreate("run", Lifespan.TRY, () -> 0);

		@Property(tries = 10)
		void run1() {
			lifespanTry.update(i -> 42);
			assertThat(lifespanTry.get()).isEqualTo(42);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanTry.get()).isEqualTo(0);
			});
		}

		@Property(tries = 10)
		void run2() {
			lifespanTry.update(i -> 42);
			assertThat(lifespanTry.get()).isEqualTo(42);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(lifespanTry.get()).isEqualTo(0);
			});
		}
	}
}
