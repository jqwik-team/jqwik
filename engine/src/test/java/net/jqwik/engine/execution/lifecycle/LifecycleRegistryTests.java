package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.TestDescriptorBuilder.*;

class LifecycleRegistryTests {

	LifecycleHooksRegistry registry = new LifecycleHooksRegistry();

	@Example
	void globalHookIsFoundInAllElements() {
		TestDescriptor engine =
			forEngine(new JqwikTestEngine())
				.with(forClass(Container1.class, "method1_1", "method1_2"))
				.with(forClass(Container2.class, "method2_1", "method2_2"))
				.build();

		GlobalHook hookInstance = new GlobalHook();
		registry.registerLifecycleInstance(engine, hookInstance);

		assertThat(registry.hasHook(engine, GlobalHook.class));
		assertThat(engine.getDescendants()).allMatch(descriptor -> registry.hasHook(descriptor, GlobalHook.class));
	}

	@Example
	void globalHookForMethodsOnly() {
		TestDescriptor engine =
			forEngine(new JqwikTestEngine())
				.with(forClass(Container1.class, "method1_1", "method1_2"))
				.with(forClass(Container2.class, "method2_1", "method2_2"))
				.build();

		GlobalHookForMethodsOnly hookInstance = new GlobalHookForMethodsOnly();
		registry.registerLifecycleInstance(engine, hookInstance);

		assertThat(!registry.hasHook(engine, GlobalHookForMethodsOnly.class));
		assertThat(engine.getDescendants().stream().filter(descriptor -> descriptor instanceof PropertyMethodDescriptor))
			.allMatch(descriptor -> registry.hasHook(descriptor, GlobalHookForMethodsOnly.class));
		assertThat(engine.getDescendants().stream().filter(descriptor -> !(descriptor instanceof PropertyMethodDescriptor)))
			.allMatch(descriptor -> !registry.hasHook(descriptor, GlobalHookForMethodsOnly.class));
	}

	@Example
	void currentDescriptorIsSetDuringRegisteringHookClass() {
		TestDescriptor container1 = forClass(Container1.class, "method1_1", "method1_2").build();
		registry.registerLifecycleHook(container1, RememberCurrentDescriptorHook.class);

		assertThat(RememberCurrentDescriptorHook.currentDescriptor).isSameAs(container1);
	}

	@Group
	@AddLifecycleHook(CheckPrepareForContainer.class)
	class Preparation {

		@Example
		void withGlobalHook_allElementsWillBePrepared() {
			TestDescriptor engine =
				forEngine(new JqwikTestEngine())
					.with(forClass(Container1.class, "method1_1", "method1_2"))
					.with(forClass(Container2.class, "method2_1", "method2_2"))
					.build();

			GlobalHook hookInstance = new GlobalHook();
			registry.registerLifecycleInstance(engine, hookInstance);

			registry.prepareHooks(engine);
			assertThat(hookInstance.preparedElements.get(0)).isNull();

			for (TestDescriptor descendant : engine.getDescendants()) {
				registry.prepareHooks(descendant);
				assertThat(hookInstance.preparedElements).contains(elementOf(descendant));
			}
		}

		@Property(tries = 10)
		@AddLifecycleHook(CheckPrepareForProperty.class)
		void propertyMethod() {
		}

		private AnnotatedElement elementOf(TestDescriptor descendant) {
			if (descendant instanceof JqwikDescriptor) {
				return ((JqwikDescriptor) descendant).getAnnotatedElement();
			}
			return null;
		}

	}

	private static class Container1 {
		@Property
		public void method1_1() {}

		@Property
		public void method1_2() {}
	}

	private static class Container2 {
		@Property
		public void method2_1() {}

		@Property
		public void method2_2() {}
	}

	class GlobalHook implements LifecycleHook, LifecycleHook.ApplyToChildren {
		List<Object> preparedElements = new ArrayList<>();

		@Override
		public void prepareFor(Optional<AnnotatedElement> element) {
			preparedElements.add(element.orElse(null));
		}
	}

	class GlobalHookForMethodsOnly implements LifecycleHook, LifecycleHook.ApplyToChildren {
		@Override
		public boolean appliesTo(Optional<AnnotatedElement> optionalElement) {
			return optionalElement.map(element -> element instanceof Method).orElse(false);
		}
	}

	static class RememberCurrentDescriptorHook implements LifecycleHook {
		static TestDescriptor currentDescriptor;

		RememberCurrentDescriptorHook() {
			this.currentDescriptor = CurrentTestDescriptor.get();
		}
	}

	static class CheckPrepareForProperty implements LifecycleHook, AroundPropertyHook {
		boolean prepareHasBeenCalled = false;

		@Override
		public void prepareFor(Optional<AnnotatedElement> element) {
			assertThat(CurrentTestDescriptor.get()).isInstanceOf(TestDescriptor.class);
			assertThat(element.isPresent());
			assertThat(element.get()).isInstanceOf(Method.class);
			prepareHasBeenCalled = true;
		}

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			try {
				return property.execute();
			} finally {
				assertThat(prepareHasBeenCalled).isTrue();
			}
		}
	}

	static class CheckPrepareForContainer implements LifecycleHook, AfterContainerHook {
		boolean prepareHasBeenCalled = false;

		@Override
		public void prepareFor(Optional<AnnotatedElement> element) {
			assertThat(CurrentTestDescriptor.get()).isInstanceOf(TestDescriptor.class);
			assertThat(element.isPresent());
			assertThat(element.get()).isInstanceOf(Class.class);
			prepareHasBeenCalled = true;
		}

		@Override
		public void afterContainer(ContainerLifecycleContext context) throws Throwable {
			assertThat(prepareHasBeenCalled).isTrue();
		}
	}
}
