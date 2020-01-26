package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.Store.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

class StorageTests {

	private final TestDescriptor engine = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

	@Property
	void cannotCreateStoreWithNullIdentifier(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(engine, null, visibility, initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void cannotCreateStoreWithNullInitializer(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		assertThatThrownBy(() -> repository.create(engine, "name", visibility, null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void cannotCreateStoreWithNullVisibility() {
		StoreRepository repository = new StoreRepository();

		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(engine, "name", null, initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void storeValueIsInitializedOnFirstAccess(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(engine, "aString", visibility, () -> "initial value");
		assertThat(store.get()).isEqualTo("initial value");
	}

	@Property
	void storeValueCanBeUpdated(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(engine, "aString", visibility, () -> "old");
		store.update((old) -> old + ":" + old);
		assertThat(store.get()).isEqualTo("old:old");
	}

	@Property
	void storeValueCanBeReset(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(engine, "aString", visibility, () -> "initial");
		store.update((old) -> "updated");
		store.reset();
		assertThat(store.get()).isEqualTo("initial");
	}

	@Property
	void removeAllStoresForScope(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> engineStore = repository.create(engine, "aString", visibility, () -> "initial");

		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
		ScopedStore<String> containerStore1 = repository.create(container, "store1", visibility, () -> "initial");
		ScopedStore<String> containerStore2 = repository.create(container, "store2", visibility, () -> "initial");

		repository.removeStoresFor(container);

		assertThat(repository.get(container, "store1")).isNotPresent();
		assertThat(repository.get(container, "store2")).isNotPresent();
		assertThat(repository.get(engine, "aString")).isPresent();
	}

	@Property
	void nullValuesAreAllowed(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(engine, "aString", visibility, () -> null);
		assertThat(store.get()).isEqualTo(null);
		store.update((old) -> "updated");
		assertThat(store.get()).isEqualTo("updated");
		store.update((old) -> null);
		assertThat(store.get()).isEqualTo(null);
	}

	@Property
	void storeCanBeRetrievedForNullScope(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(null, "aString", visibility, () -> "value");
		Optional<ScopedStore<String>> optionalStore = repository.get(null, "aString");
		assertThat(optionalStore.get()).isSameAs(store);
	}

	@Group
	class Local_Storage {

		@Example
		void canCreateTwoStoresWithSameNameInDifferentScopes() {

			StoreRepository repository = new StoreRepository();

			TestDescriptor container1 = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container1, "aString", Visibility.LOCAL, () -> "initial");

			TestDescriptor container2 = TestDescriptorBuilder.forClass(Container2.class).build();
			repository.create(container2, "aString", Visibility.LOCAL, () -> "initial");
			
			Optional<ScopedStore<String>> optionalStore1 = repository.get(container1, "aString");
			assertThat(optionalStore1).isPresent();

			Optional<ScopedStore<String>> optionalStore2 = repository.get(container2, "aString");
			assertThat(optionalStore1).isPresent();

			assertThat(optionalStore1).isNotEqualTo(optionalStore2);
		}

		@Example
		void cannotCreateTwoLocalStoresWithSameNameInSameScope() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container, "aStore", Visibility.LOCAL, () -> "initial");

			assertThatThrownBy(() -> {
				repository.create(container, "aStore", Visibility.LOCAL, () -> 42);
			}).isInstanceOf(JqwikException.class);
		}


		@Example
		void canBeRetrievedForSameScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			ScopedStore<String> store = repository.create(container, "aString", Visibility.LOCAL, () -> "initial");
			ScopedStore<String> otherStore = repository.create(container, "otherString", Visibility.LOCAL, () -> "initial");

			Optional<ScopedStore<String>> optionalStore = repository.get(container, "aString");
			assertThat(optionalStore).isPresent();
			assertThat(optionalStore.get()).isSameAs(store);

			Optional<ScopedStore<String>> optionalOtherStore = repository.get(container, "otherString");
			assertThat(optionalOtherStore).isPresent();
			assertThat(optionalOtherStore.get()).isSameAs(otherStore);
		}

		@Example
		void cannotBeRetrievedForChildScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			ScopedStore<String> store = repository.create(container, "aString", Visibility.LOCAL, () -> "initial");

			TestDescriptor method1 = container.getChildren().iterator().next();

			Optional<ScopedStore<String>> optionalStore = repository.get(method1, "aString");
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void cannotBeRetrievedForSameScopeAndDifferentName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container, "aString", Visibility.LOCAL, () -> "initial");

			Optional<ScopedStore<String>> optionalStore = repository.get(container, "otherString");
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void cannotBeRetrievedForUnrelatedScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor owner = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(owner, "aString", Visibility.LOCAL, () -> "initial");

			TestDescriptor otherOwner = TestDescriptorBuilder.forClass(Container2.class).build();

			Optional<ScopedStore<String>> optionalStore = repository.get(otherOwner, "aString");
			assertThat(optionalStore).isNotPresent();
		}

	}

	private static class Container1 {

		@Property
		void method1() {
		}

		@Property
		void method2() {
		}

	}

	private static class Container2 {

	}
}
