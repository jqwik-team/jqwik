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
	void cannotCreateStoreWithEmptyName(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(visibility, engine, "", initializer))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> repository.create(visibility, engine, null, initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void cannotCreateStoreWithNullInitializer(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		assertThatThrownBy(() -> repository.create(visibility, engine, "name", null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void cannotCreateStoreWithNullVisibility() {
		StoreRepository repository = new StoreRepository();

		Supplier<String> initializer = () -> "a String";

		assertThatThrownBy(() -> repository.create(null, engine, "name", initializer))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void storeValueIsInitializedOnFirstAccess(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(visibility, engine, "aString", () -> "initial value");
		assertThat(store.get(engine)).isEqualTo("initial value");
	}

	@Property
	void storeValueCanBeUpdated(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(visibility, engine, "aString", () -> "old");
		store.update(engine, (old) -> old + ":" + old);
		assertThat(store.get(engine)).isEqualTo("old:old");
	}

	@Property
	void storeValueCanBeReset(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(visibility, engine, "aString", () -> "initial");
		store.update(engine, (old) -> "updated");
		store.reset(engine);
		assertThat(store.get(engine)).isEqualTo("initial");
	}

	@Property
	void nullValuesAreAllowed(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(visibility, engine, "aString", () -> null);
		assertThat(store.get(engine)).isEqualTo(null);
		store.update(engine, (old) -> "updated");
		assertThat(store.get(engine)).isEqualTo("updated");
		store.update(engine, (old) -> null);
		assertThat(store.get(engine)).isEqualTo(null);
	}

	@Property
	void storeCanBeRetrievedForNullScope(@ForAll Visibility visibility) {
		StoreRepository repository = new StoreRepository();

		ScopedStore<String> store = repository.create(visibility, null, "aString", () -> "value");
		Optional<ScopedStore<String>> optionalStore = repository.get(null, "aString", String.class);
		assertThat(optionalStore.get()).isSameAs(store);
	}

	@Group
	class Local_Storage {

		@Example
		void twoDistinctStoresWithSameNameInUnrelatedScopesArePossible() {

			fail("how should this really work?");

			StoreRepository repository = new StoreRepository();

			TestDescriptor container1 = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(Visibility.LOCAL, container1, "aString", () -> "initial");

			TestDescriptor container2 = TestDescriptorBuilder.forClass(Container2.class).build();
			repository.create(Visibility.LOCAL, container2, "aString", () -> "initial");
			
			Optional<ScopedStore<String>> optionalStore1 = repository.get(container1, "aString", String.class);
			assertThat(optionalStore1).isPresent();

			Optional<ScopedStore<String>> optionalStore2 = repository.get(container2, "aString", String.class);
			assertThat(optionalStore1).isPresent();

			assertThat(optionalStore1).isNotEqualTo(optionalStore2);
		}


		@Example
		void canBeRetrievedForSameScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			ScopedStore<String> store = repository.create(Visibility.LOCAL, container, "aString", () -> "initial");
			ScopedStore<String> otherStore = repository.create(Visibility.LOCAL, container, "otherString", () -> "initial");

			Optional<ScopedStore<String>> optionalStore = repository.get(container, "aString", String.class);
			assertThat(optionalStore).isPresent();
			assertThat(optionalStore.get()).isSameAs(store);

			Optional<ScopedStore<String>> optionalOtherStore = repository.get(container, "otherString", String.class);
			assertThat(optionalOtherStore).isPresent();
			assertThat(optionalOtherStore.get()).isSameAs(otherStore);
		}

		@Example
		void canBeRetrievedForChildScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			ScopedStore<String> store = repository.create(Visibility.LOCAL, container, "aString", () -> "initial");

			TestDescriptor method1 = container.getChildren().iterator().next();

			Optional<ScopedStore<String>> optionalStore = repository.get(method1, "aString", String.class);
			assertThat(optionalStore).isPresent();
			assertThat(optionalStore.get()).isSameAs(store);
		}

		@Example
		void cannotBeRetrievedForSameScopeAndDifferentName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(Visibility.LOCAL, container, "aString", () -> "initial");

			Optional<ScopedStore<String>> optionalStore = repository.get(container, "otherString", String.class);
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void cannotBeRetrievedForUnrelatedScopeAndSameName() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor owner = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(Visibility.LOCAL, owner, "aString", () -> "initial");

			TestDescriptor otherOwner = TestDescriptorBuilder.forClass(Container2.class).build();

			Optional<ScopedStore<String>> optionalStore = repository.get(otherOwner, "aString", String.class);
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void storeValuesAreIndependentForDifferentRetrievers() {
			StoreRepository repository = new StoreRepository();

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1", "method2").build();
			Iterator<? extends TestDescriptor> children = container.getChildren().iterator();

			TestDescriptor method1 = children.next();
			TestDescriptor method2 = children.next();

			repository.create(Visibility.LOCAL, container, "aString", () -> "initial");

			ScopedStore<String> scopedStore = repository.get(container, "aString", String.class).get();

			scopedStore.update(container, (old) -> "container value");
			scopedStore.update(method1, (old) -> "method1 value");
			scopedStore.update(method2, (old) -> "method2 value");

			assertThat(scopedStore.get(container)).isEqualTo("container value");
			assertThat(scopedStore.get(method1)).isEqualTo("method1 value");
			assertThat(scopedStore.get(method2)).isEqualTo("method2 value");
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
