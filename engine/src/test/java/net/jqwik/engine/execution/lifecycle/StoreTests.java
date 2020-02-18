package net.jqwik.engine.execution.lifecycle;

import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;
import net.jqwik.engine.*;

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

	@Example
	void storeValueIsInitializedOnFirstAccess() {
		Store<String> store = Store.create("aString", Lifespan.PROPERTY, () -> "initial value");
		assertThat(store.get()).isEqualTo("initial value");
	}

	@Example
	void storeValueCanBeUpdated() {
		Store<String> store = Store.create("aString", Lifespan.PROPERTY, () -> "old");
		store.update((old) -> old + ":" + old);
		assertThat(store.get()).isEqualTo("old:old");
	}

	@Example
	void storeValueCanBeReset() {
		Store<String> store = Store.create("aString", Lifespan.PROPERTY, () -> "initial");
		store.update((old) -> "updated");
		store.reset();
		assertThat(store.get()).isEqualTo("initial");
	}

	@Example
	void nullValuesAreAllowed() {
		Store<String> store = Store.create("aString", Lifespan.PROPERTY, () -> null);
		assertThat(store.get()).isEqualTo(null);
		store.update((old) -> "updated");
		assertThat(store.get()).isEqualTo("updated");
		store.update((old) -> null);
		assertThat(store.get()).isEqualTo(null);
	}

	@SuppressWarnings("unchecked")
	@Example
	void resettingStoreCallsOnCloseCallbacks() {
		Consumer<String> onClose1 = Mockito.mock(Consumer.class);
		Consumer<String> onClose2 = Mockito.mock(Consumer.class);
		Store<String> store = Store
								  .create("aString", Lifespan.PROPERTY, () -> "value")
								  .onClose(onClose1)
								  .onClose(onClose2);
		store.get(); // to invoke initialization
		store.reset();

		Mockito.verify(onClose1, Mockito.times(1)).accept("value");
		Mockito.verify(onClose2, Mockito.times(1)).accept("value");
	}

	@SuppressWarnings("unchecked")
	@SuppressLogging("severe logging of exception expected")
	@Example
	void swallowExceptionsInOnCloseCallbacks() {
		Consumer<String> onClose1 = Mockito.mock(Consumer.class);
		Mockito.doThrow(new RuntimeException("for testing")).when(onClose1).accept("value");
		Consumer<String> onClose2 = Mockito.mock(Consumer.class);
		Store<String> store = Store
								  .create("aString", Lifespan.PROPERTY, () -> "value")
								  .onClose(onClose1)
								  .onClose(onClose2);
		store.get(); // to invoke initialization
		store.reset();

		Mockito.verify(onClose1, Mockito.times(1)).accept("value");
		Mockito.verify(onClose2, Mockito.times(1)).accept("value");
	}

	class AssertCounter110 implements Lifecycle {
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
