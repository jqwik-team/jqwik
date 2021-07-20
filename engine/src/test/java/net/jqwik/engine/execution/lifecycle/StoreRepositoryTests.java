package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@Group
class StoreRepositoryTests {

	private final TestDescriptor engine = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

	private StoreRepository repository = new StoreRepository();

	@Group
	class Creation {

		@Example
		void cannotCreateStoreWithNullScope() {
			Supplier<String> initializer = () -> "a String";

			assertThatThrownBy(() -> repository.create(null, "name", Lifespan.PROPERTY, initializer))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void cannotCreateStoreWithNullIdentifier() {
			Supplier<String> initializer = () -> "a String";

			assertThatThrownBy(() -> repository.create(engine, null, Lifespan.PROPERTY, initializer))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void cannotCreateStoreWithNullLifespan() {
			Supplier<String> initializer = () -> "a String";

			assertThatThrownBy(() -> repository.create(engine, "store", null, initializer))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void cannotCreateStoreWithNullInitializer() {
			assertThatThrownBy(() -> repository.create(engine, "name", Lifespan.PROPERTY, null))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void canCreateTwoStoresWithSameNameInDifferentScopes() {
			TestDescriptor container1 = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container1, "aString", Lifespan.PROPERTY, () -> "initial");

			TestDescriptor container2 = TestDescriptorBuilder.forClass(Container2.class).build();
			repository.create(container2, "aString", Lifespan.PROPERTY, () -> "initial");

			Optional<ScopedStore<String>> optionalStore1 = repository.get(container1, "aString");
			assertThat(optionalStore1).isPresent();

			Optional<ScopedStore<String>> optionalStore2 = repository.get(container2, "aString");
			assertThat(optionalStore2).isPresent();

			assertThat(optionalStore1).isNotEqualTo(optionalStore2);
		}

		@Example
		void cannotCreateTwoLocalStoresWithSameNameInSameScope() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container, "aStore", Lifespan.PROPERTY, () -> "initial");

			assertThatThrownBy(() -> {
				repository.create(container, "aStore", Lifespan.PROPERTY, () -> 42);
			}).isInstanceOf(JqwikException.class);
		}

		@Example
		void cannotCreateAnotherLocalStoresWithSameNameInParentScope() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			TestDescriptor method1 = container.getChildren().iterator().next();

			repository.create(method1, "aStore", Lifespan.PROPERTY, () -> "initial");

			assertThatThrownBy(() -> {
				repository.create(container, "aStore", Lifespan.PROPERTY, () -> 42);
			}).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	class Retrieval {

		@Example
		void canBeRetrievedForSameScopeAndSameName() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			ScopedStore<String> store = repository.create(container, "aString", Lifespan.PROPERTY, () -> "initial");
			ScopedStore<String> otherStore = repository.create(container, "otherString", Lifespan.PROPERTY, () -> "initial");

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
			ScopedStore<String> store = repository.create(container, "aString", Lifespan.PROPERTY, () -> "initial");

			TestDescriptor method1 = container.getChildren().iterator().next();

			Optional<ScopedStore<String>> optionalStore = repository.get(method1, "aString");
			assertThat(optionalStore).isPresent();
			assertThat(optionalStore.get()).isSameAs(store);
		}

		@Example
		void cannotBeRetrievedForSameScopeAndDifferentName() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(container, "aString", Lifespan.PROPERTY, () -> "initial");

			Optional<ScopedStore<String>> optionalStore = repository.get(container, "otherString");
			assertThat(optionalStore).isNotPresent();
		}

		@Example
		void cannotBeRetrievedForUnrelatedScopeAndSameName() {
			TestDescriptor owner = TestDescriptorBuilder.forClass(Container1.class).build();
			repository.create(owner, "aString", Lifespan.PROPERTY, () -> "initial");

			TestDescriptor otherOwner = TestDescriptorBuilder.forClass(Container2.class).build();

			Optional<ScopedStore<String>> optionalStore = repository.get(otherOwner, "aString");
			assertThat(optionalStore).isNotPresent();
		}

	}

	@Group
	class Lifecycles_and_Lifespans {

		@Example
		void finishTry_resetsAllVisibleStoresWithLifespanTry() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			ScopedStore<String> containerStoreTry = repository.create(container, "containerStoreTry", Lifespan.TRY, () -> "initial");
			containerStoreTry.update(s -> "changed");
			ScopedStore<String> containerStoreRun = repository.create(container, "containerStoreRun", Lifespan.RUN, () -> "initial");
			containerStoreRun.update(s -> "changed");

			TestDescriptor method = container.getChildren().iterator().next();
			ScopedStore<String> methodStoreTry = repository.create(method, "methodStoreTry", Lifespan.TRY, () -> "initial");
			methodStoreTry.update(s -> "changed");
			ScopedStore<String> methodStoreProperty = repository.create(method, "methodStoreProperty", Lifespan.PROPERTY, () -> "initial");
			methodStoreProperty.update(s -> "changed");

			TestDescriptor otherContainer = TestDescriptorBuilder.forClass(Container2.class).build();
			ScopedStore<String> otherContainerStoreTry = repository
															 .create(otherContainer, "otherContainerStoreTry", Lifespan.TRY, () -> "initial");
			otherContainerStoreTry.update(s -> "changed");

			repository.finishTry(method);

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(containerStoreTry.get()).isEqualTo("initial");
				softly.assertThat(containerStoreRun.get()).isEqualTo("changed");
				softly.assertThat(methodStoreTry.get()).isEqualTo("initial");
				softly.assertThat(methodStoreProperty.get()).isEqualTo("changed");
				softly.assertThat(otherContainerStoreTry.get()).isEqualTo("changed");
			});
		}

		@Example
		void finishProperty_resetsAllVisibleStoresWithLifespanProperty() {
			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			ScopedStore<String> containerStoreProperty = repository
															 .create(container, "containerStoreProperty", Lifespan.PROPERTY, () -> "initial");
			containerStoreProperty.update(s -> "changed");
			ScopedStore<String> containerStoreRun = repository.create(container, "containerStoreRun", Lifespan.RUN, () -> "initial");
			containerStoreRun.update(s -> "changed");

			TestDescriptor method = container.getChildren().iterator().next();
			ScopedStore<String> methodStoreProperty = repository.create(method, "methodStoreProperty", Lifespan.PROPERTY, () -> "initial");
			methodStoreProperty.update(s -> "changed");
			ScopedStore<String> methodStoreTry = repository.create(method, "methodStoreTry", Lifespan.TRY, () -> "initial");
			methodStoreTry.update(s -> "changed");

			TestDescriptor otherContainer = TestDescriptorBuilder.forClass(Container2.class).build();
			ScopedStore<String> otherContainerStoreProperty = repository
																  .create(otherContainer, "otherContainerStoreProperty", Lifespan.PROPERTY, () -> "initial");
			otherContainerStoreProperty.update(s -> "changed");

			repository.finishProperty(method);

			SoftAssertions.assertSoftly(softly -> {
				softly.assertThat(containerStoreProperty.get()).isEqualTo("initial");
				softly.assertThat(containerStoreRun.get()).isEqualTo("changed");
				softly.assertThat(methodStoreProperty.get()).isEqualTo("initial");
				softly.assertThat(methodStoreTry.get()).isEqualTo("changed");
				softly.assertThat(otherContainerStoreProperty.get()).isEqualTo("changed");
			});
		}

		@Example
		void finishScope_removesAllStoresForScopeAndItsChildren() {
			TestDescriptor container1 = TestDescriptorBuilder.forClass(Container1.class, "method1").build();
			TestDescriptor method1 = container1.getChildren().iterator().next();

			ScopedStore<String> containerStore1 = repository.create(container1, "store1", Lifespan.PROPERTY, () -> "initial");
			ScopedStore<String> containerStore2 = repository.create(container1, "store2", Lifespan.PROPERTY, () -> "initial");
			ScopedStore<String> method1Store = repository.create(method1, "method1Store", Lifespan.PROPERTY, () -> "initial");

			repository.finishScope(container1);

			TestDescriptor container2 = TestDescriptorBuilder.forClass(Container2.class).build();
			ScopedStore<String> container2Store = repository.create(container2, "container2store", Lifespan.PROPERTY, () -> "initial");

			assertThat(repository.get(container1, "store1")).isNotPresent();
			assertThat(repository.get(container1, "store2")).isNotPresent();
			assertThat(repository.get(method1, "method1Store")).isNotPresent();
			assertThat(repository.get(container2, "container2store")).isPresent();
		}

		@SuppressWarnings("unchecked")
		@Example
		void finishScope_callsCloseOnAllRemovedStoreValues() {
			Consumer<String> onCloseContainerStore = Mockito.mock(Consumer.class);
			Consumer<String> onCloseMethodStore = Mockito.mock(Consumer.class);
			Consumer<String> onCloseUninitializedMethodStore = Mockito.mock(Consumer.class);

			TestDescriptor container = TestDescriptorBuilder.forClass(Container1.class, "method1", "method2").build();
			Iterator<? extends TestDescriptor> methods = container.getChildren().iterator();
			TestDescriptor method = methods.next();
			TestDescriptor otherMethod = methods.next();

			ScopedStore<String> containerStore =
				repository
					.create(container, "containerStore", Lifespan.PROPERTY, () -> "container value")
					.onClose(onCloseContainerStore);
			containerStore.get(); // to invoke initialization

			ScopedStore<String> methodStore =
				repository
					.create(method, "methodStore", Lifespan.PROPERTY, () -> "method value")
					.onClose(onCloseMethodStore);
			methodStore.get(); // to invoke initialization

			ScopedStore<String> uninitializedMethodStore =
				repository
					.create(otherMethod, "uninitializedMethodStore", Lifespan.PROPERTY, () -> "other method value")
					.onClose(onCloseUninitializedMethodStore);

			repository.finishScope(container);

			Mockito.verify(onCloseContainerStore, Mockito.times(1)).accept(anyString());
			Mockito.verify(onCloseMethodStore, Mockito.times(1)).accept(anyString());
			Mockito.verify(onCloseUninitializedMethodStore, Mockito.never()).accept(anyString());
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
