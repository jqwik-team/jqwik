package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.TestDescriptorBuilder.*;

class LifecycleRegistryTests {

	public static final Function<String, Optional<String>> NO_CONFIGURATION_PARAMETERS = key -> Optional.empty();
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
	void currentDescriptorWhenRegisteringHookClass() {
		TestDescriptor container1 = forClass(Container1.class, "method1_1", "method1_2").build();
		registry.registerLifecycleHook(container1, RememberCurrentDescriptorHook.class, NO_CONFIGURATION_PARAMETERS);

		assertThat(RememberCurrentDescriptorHook.currentDescriptor).isSameAs(container1);
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
}
