package net.jqwik.engine.execution.lifecycle;

import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;

class LifecycleContextToStringTests {

	private final Reporter reporter = Mockito.mock(Reporter.class);
	private final ResolveParameterHook resolveParameter = Mockito.mock(ResolveParameterHook.class);

	@Example
	void engineLifecycleContext() {

		TestDescriptor engineDescriptor = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();
		EngineLifecycleContext lifecycleContext = new EngineLifecycleContext(
			engineDescriptor,
			reporter,
			resolveParameter
		);

		Assertions.assertThat(lifecycleContext.toString())
				  .isEqualTo("ContainerLifecycleContext([engine:jqwik])");
	}

	@Example
	void containerLifecycleDescriptor() {

		ContainerClassDescriptor containerDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(LifecycleContextToStringTests.class).build();
		DefaultContainerLifecycleContext lifecycleContext = new DefaultContainerLifecycleContext(
			containerDescriptor,
			reporter,
			resolveParameter
		);

		Assertions.assertThat(lifecycleContext.toString())
				  .isEqualTo("ContainerLifecycleContext([engine:jqwik]/" +
								 "[class:net.jqwik.engine.execution.lifecycle.LifecycleContextToStringTests]" +
								 ")");
	}

	@Example
	void nestedContainerLifecycleDescriptor() {

		ContainerClassDescriptor parent = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(LifecycleContextToStringTests.class).build();
		ContainerClassDescriptor containerDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(InnerTests.class).build(parent);
		DefaultContainerLifecycleContext lifecycleContext = new DefaultContainerLifecycleContext(
			containerDescriptor,
			reporter,
			resolveParameter
		);

		Assertions.assertThat(lifecycleContext.toString())
				  .isEqualTo("ContainerLifecycleContext([engine:jqwik]/" +
								 "[class:net.jqwik.engine.execution.lifecycle.LifecycleContextToStringTests]/" +
								 "[class:InnerTests]" +
								 ")");
	}

	@Example
	void propertyLifecycleDescriptor() {

		PropertyMethodDescriptor engineDescriptor = (PropertyMethodDescriptor) TestDescriptorBuilder.forMethod(
			InnerTests.class, "aProperty", String.class
		).build();
		DefaultPropertyLifecycleContext lifecycleContext = new DefaultPropertyLifecycleContext(
			engineDescriptor,
			new InnerTests(),
			reporter,
			resolveParameter
		);

		Assertions.assertThat(lifecycleContext.toString())
				  .isEqualTo("PropertyLifecycleContext([engine:jqwik]/" +
								 "[class:net.jqwik.engine.execution.lifecycle.LifecycleContextToStringTests]/" +
								 "[class:InnerTests]/" +
								 "[property:aProperty(java.lang.String)]" +
								 ")");
	}


	@Example
	void tryLifecycleDescriptor() {

		PropertyMethodDescriptor engineDescriptor = (PropertyMethodDescriptor) TestDescriptorBuilder.forMethod(
			InnerTests.class, "aProperty", String.class
		).build();
		DefaultPropertyLifecycleContext propertyLifecycleContext = new DefaultPropertyLifecycleContext(
			engineDescriptor,
			new InnerTests(),
			reporter,
			resolveParameter
		);
		DefaultTryLifecycleContext tryLifecycleContext = new DefaultTryLifecycleContext(propertyLifecycleContext);

		Assertions.assertThat(tryLifecycleContext.toString())
				  .isEqualTo("TryLifecycleContext:PropertyLifecycleContext([engine:jqwik]/" +
								 "[class:net.jqwik.engine.execution.lifecycle.LifecycleContextToStringTests]/" +
								 "[class:InnerTests]/" +
								 "[property:aProperty(java.lang.String)]" +
								 ")");
	}

	@Group
	private class InnerTests {
		@Property
		void aProperty(@ForAll String aString) {}
	}
}
