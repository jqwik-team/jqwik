package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

// TODO: Add Lifespan
class StoreRepositoryTests {

	private final TestDescriptor engine = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

	private StoreRepository repository = new StoreRepository();

	@Example
	void cannotCreateStoreWithNullIdentifier() {
		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(engine, null, initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void cannotCreateStoreWithNullInitializer() {
		assertThatThrownBy(() -> repository.create(engine, "name", null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void storeValueIsInitializedOnFirstAccess() {
		ScopedStore<String> store = repository.create(engine, "aString", () -> "initial value");
		assertThat(store.get()).isEqualTo("initial value");
	}

	@Example
	void storeValueCanBeUpdated() {
		ScopedStore<String> store = repository.create(engine, "aString", () -> "old");
		store.update((old) -> old + ":" + old);
		assertThat(store.get()).isEqualTo("old:old");
	}

	@Example
	void storeValueCanBeReset() {
		ScopedStore<String> store = repository.create(engine, "aString", () -> "initial");
		store.update((old) -> "updated");
		store.reset();
		assertThat(store.get()).isEqualTo("initial");
	}

	@Example
	void removeAllStoresForScope() {
		ScopedStore<String> engineStore = repository.create(engine, "aString", () -> "initial");

		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
		ScopedStore<String> containerStore1 = repository.create(container, "store1", () -> "initial");
		ScopedStore<String> containerStore2 = repository.create(container, "store2", () -> "initial");

		repository.removeStoresFor(container);

		assertThat(repository.get(container, "store1")).isNotPresent();
		assertThat(repository.get(container, "store2")).isNotPresent();
		assertThat(repository.get(engine, "aString")).isPresent();
	}

	@Example
	void nullValuesAreAllowed() {
		ScopedStore<String> store = repository.create(engine, "aString", () -> null);
		assertThat(store.get()).isEqualTo(null);
		store.update((old) -> "updated");
		assertThat(store.get()).isEqualTo("updated");
		store.update((old) -> null);
		assertThat(store.get()).isEqualTo(null);
	}

	@Example
	void storeCanBeRetrievedForNullScope() {
		ScopedStore<String> store = repository.create(null, "aString", () -> "value");
		Optional<ScopedStore<String>> optionalStore = repository.get(null, "aString");
		assertThat(optionalStore.get()).isSameAs(store);
	}

	@Example
	void canCreateTwoStoresWithSameNameInDifferentScopes() {
		TestDescriptor container1 = TestDescriptorBuilder.forClass(Container1.class).build();
		repository.create(container1, "aString", () -> "initial");

		TestDescriptor container2 = TestDescriptorBuilder.forClass(Container2.class).build();
		repository.create(container2, "aString", () -> "initial");

		Optional<ScopedStore<String>> optionalStore1 = repository.get(container1, "aString");
		assertThat(optionalStore1).isPresent();

		Optional<ScopedStore<String>> optionalStore2 = repository.get(container2, "aString");
		assertThat(optionalStore1).isPresent();

		assertThat(optionalStore1).isNotEqualTo(optionalStore2);
	}

	@Example
	void cannotCreateTwoLocalStoresWithSameNameInSameScope() {
		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
		repository.create(container, "aStore", () -> "initial");

		assertThatThrownBy(() -> {
			repository.create(container, "aStore", () -> 42);
		}).isInstanceOf(JqwikException.class);
	}

	@Example
	void canBeRetrievedForSameScopeAndSameName() {
		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
		ScopedStore<String> store = repository.create(container, "aString", () -> "initial");
		ScopedStore<String> otherStore = repository.create(container, "otherString", () -> "initial");

		Optional<ScopedStore<String>> optionalStore = repository.get(container, "aString");
		assertThat(optionalStore).isPresent();
		assertThat(optionalStore.get()).isSameAs(store);

		Optional<ScopedStore<String>> optionalOtherStore = repository.get(container, "otherString");
		assertThat(optionalOtherStore).isPresent();
		assertThat(optionalOtherStore.get()).isSameAs(otherStore);
	}

	@Example
	void canBeRetrievedForChildScopeAndSameName() {
		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
		ScopedStore<String> store = repository.create(container, "aString", () -> "initial");

		TestDescriptor method1 = container.getChildren().iterator().next();

		Optional<ScopedStore<String>> optionalStore = repository.get(method1, "aString");
		assertThat(optionalStore).isPresent();
		assertThat(optionalStore.get()).isSameAs(store);
	}

	@Example
	void cannotBeRetrievedForSameScopeAndDifferentName() {
		TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
		repository.create(container, "aString", () -> "initial");

		Optional<ScopedStore<String>> optionalStore = repository.get(container, "otherString");
		assertThat(optionalStore).isNotPresent();
	}

	@Example
	void cannotBeRetrievedForUnrelatedScopeAndSameName() {
		TestDescriptor owner = TestDescriptorBuilder.forClass(Container1.class).build();
		repository.create(owner, "aString", () -> "initial");

		TestDescriptor otherOwner = TestDescriptorBuilder.forClass(Container2.class).build();

		Optional<ScopedStore<String>> optionalStore = repository.get(otherOwner, "aString");
		assertThat(optionalStore).isNotPresent();
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
