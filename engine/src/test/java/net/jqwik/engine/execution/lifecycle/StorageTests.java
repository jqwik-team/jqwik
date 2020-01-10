package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.Store.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

class StorageTests {

	@Property
	void cannotCreateStoreWithEmptyName(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		TestDescriptor owner = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();
		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(visibility, owner, "", initializer))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> repository.create(visibility, owner, null, initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void cannotCreateStoreWithNullInitializer(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		TestDescriptor owner = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

		assertThatThrownBy(() -> repository.create(visibility, owner, "name", null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void cannotCreateStoreWithNullVisibility() {
		StoreRepository repository = new StoreRepository();

		TestDescriptor owner = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();
		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(null, owner, "name", initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void storeValueIsInitializedOnFirstAccess(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Store<String> store = repository.create(visibility, null, "aString", () -> "initial value");
		assertThat(store.get()).isEqualTo("initial value");
	}

	@Property
	void storeValueCanBeUpdated(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Store<String> store = repository.create(visibility, null, "aString", () -> "old");
		store.update((old) -> old + ":" + old);
		assertThat(store.get()).isEqualTo("old:old");
	}

	@Property
	void storeValueCanBeReset(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Store<String> store = repository.create(visibility, null, "aString", () -> "initial");
		store.update((old) -> "updated");
		store.reset();
		assertThat(store.get()).isEqualTo("initial");
	}

	@Property
	void nullValuesAreAllowed(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Store<String> store = repository.create(visibility, null, "aString", () -> null);
		assertThat(store.get()).isEqualTo(null);
		store.update((old) -> "updated");
		assertThat(store.get()).isEqualTo("updated");
		store.update((old) -> null);
		assertThat(store.get()).isEqualTo(null);
	}

	@Property
	void storeCanBeRetrievedForNullOwner(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Store<String> store = repository.create(visibility, null, "aString", () -> "value");
		Optional<Store<String>> optionalStore = repository.get(null, "aString", String.class);
		assertThat(optionalStore.get()).isSameAs(store);
	}

	@Group
	class Local_Storage {

		@Example
		void canBeRetrievedForSameOwnerAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor owner = TestDescriptorBuilder.forClass(getClass()).build();
			Store<String> store = repository.create(Visibility.LOCAL, owner, "aString", () -> "initial");
			Store<String> otherStore = repository.create(Visibility.LOCAL, owner, "otherString", () -> "initial");

			Optional<Store<String>> optionalStore = repository.get(owner, "aString", String.class);
			assertThat(optionalStore).isPresent();
			assertThat(optionalStore.get()).isSameAs(store);

			Optional<Store<String>> optionalOtherStore = repository.get(owner, "otherString", String.class);
			assertThat(optionalOtherStore).isPresent();
			assertThat(optionalOtherStore.get()).isSameAs(otherStore);
		}

		@Example
		void cannotBeRetrievedForSameOwnerAndDifferentName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor owner = TestDescriptorBuilder.forClass(getClass()).build();
			repository.create(Visibility.LOCAL, owner, "aString", () -> "initial");

			Optional<Store<String>> optionalStore = repository.get(owner, "otherString", String.class);
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void cannotBeRetrievedForOtherOwnerAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor owner = TestDescriptorBuilder.forClass(getClass()).build();
			repository.create(Visibility.LOCAL, owner, "aString", () -> "initial");

			TestDescriptor otherOwner = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

			Optional<Store<String>> optionalStore = repository.get(otherOwner, "aString", String.class);
			assertThat(optionalStore).isNotPresent();
		}

	}
}
