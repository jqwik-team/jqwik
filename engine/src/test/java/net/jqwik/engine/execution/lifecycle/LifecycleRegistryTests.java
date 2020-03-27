package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.lifecycle.PropagationMode.*;
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
		registry.registerLifecycleHook(container1, RememberCurrentDescriptorHook.class, NO_DESCENDANTS);

		assertThat(RememberCurrentDescriptorHook.currentDescriptor).isSameAs(container1);
	}

	@Group
	class UsingRegistrar {
		@Example
		void registerThroughRegistrar() {
			TestDescriptor container1 = forClass(Container1.class, "method1_1", "method1_2").build();
			registry.registerLifecycleHook(container1, RegisterGlobalHook.class, NO_DESCENDANTS);
			assertThat(registry.hasHook(container1, GlobalHook.class));
			assertThat(container1.getDescendants()).allMatch(child -> registry.hasHook(child, GlobalHook.class));
		}

		@Example
		void registerThroughRegistrar_withExplicitPropagation() {
			TestDescriptor container1 = forClass(Container1.class, "method1_1", "method1_2").build();
			registry.registerLifecycleHook(container1, RegisterGlobalHookNoDescendants.class, NO_DESCENDANTS);
			assertThat(registry.hasHook(container1, GlobalHook.class));
			assertThat(container1.getDescendants()).allMatch(child -> !registry.hasHook(child, GlobalHook.class));
		}
	}

	@Group
	@AddLifecycleHook(value = ChangeFirstParamTo42.class, propagateTo = ALL_DESCENDANTS)
	@AddLifecycleHook(value = ChangeSecondParamToAAA.class, propagateTo = DIRECT_DESCENDANTS)
	class Propagation {

		@Example
		void exampleInDirectDescendant(@ForAll int anInt, @ForAll String aString) {
			assertThat(anInt).isEqualTo(42);
			assertThat(aString).isEqualTo("AAA");
		}

		@Group
		class NestedPropagation {
			@Example
			void exampleInNestedDescendant(@ForAll int anInt, @ForAll String aString) {
				assertThat(anInt).isEqualTo(42);
				assertThat(aString).isNotEqualTo("AAA");
			}
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

	private static class GlobalHook implements LifecycleHook {
		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}
	}

	private static class RegisterGlobalHook implements RegistrarHook {
		@Override
		public void registerHooks(Registrar registrar) {
			registrar.register(GlobalHook.class);
		}
	}

	private static class RegisterGlobalHookNoDescendants implements RegistrarHook {
		@Override
		public void registerHooks(Registrar registrar) {
			registrar.register(GlobalHook.class, NO_DESCENDANTS);
		}
	}

	private static class GlobalHookForMethodsOnly implements LifecycleHook {
		@Override
		public boolean appliesTo(Optional<AnnotatedElement> optionalElement) {
			return optionalElement.map(element -> element instanceof Method).orElse(false);
		}

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}
	}

	static class RememberCurrentDescriptorHook implements LifecycleHook {
		static TestDescriptor currentDescriptor;

		RememberCurrentDescriptorHook() {
			currentDescriptor = CurrentTestDescriptor.get();
		}
	}

	static class ChangeFirstParamTo42 implements AroundTryHook {
		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
			if (parameters.size() >= 1) {
				parameters.set(0, 42);
			}
			return aTry.execute(parameters);
		}
	}

	static class ChangeSecondParamToAAA implements AroundTryHook {
		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
			if (parameters.size() >= 2) {
				parameters.set(1, "AAA");
			}
			return aTry.execute(parameters);
		}
	}
}
