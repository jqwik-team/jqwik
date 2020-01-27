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

	@Group
	class StoreBelongsToContainer {
		Store<Integer> counter = Store.getOrCreate("counter", Lifespan.RUN, () -> 0);

		@Example
		void example1() {
			counter.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(counter.get()).isEqualTo(1);
			});
		}

		@Example
		void example2() {
			counter.update(i -> i + 1);
			PropertyLifecycle.onSuccess(() -> {
				assertThat(counter.get()).isEqualTo(2);
			});
		}
	}
}
