package net.jqwik.engine.execution.lifecycle;

import java.lang.annotation.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;

import static org.assertj.core.api.Assertions.*;

@MyAnnotation
class LifecycleContextAnnotationsTests {

	private final Reporter reporter = Mockito.mock(Reporter.class);
	private final ResolveParameterHook resolveParameter = Mockito.mock(ResolveParameterHook.class);

	@Example
	void containerLifecycleDescriptor() {

		ContainerClassDescriptor containerDescriptor =
			(ContainerClassDescriptor) TestDescriptorBuilder.forClass(LifecycleContextAnnotationsTests.class).build();
		DefaultContainerLifecycleContext lifecycleContext = new DefaultContainerLifecycleContext(
			containerDescriptor,
			reporter,
			resolveParameter
		);

		assertThat(lifecycleContext.findAnnotation(MyAnnotation.class)).isPresent();
		assertThat(lifecycleContext.findAnnotationsInContainer(MyAnnotation.class)).isEmpty();
		assertThat(lifecycleContext.findAnnotation(Property.class)).isNotPresent();
	}

	@Example
	void nestedContainerLifecycleDescriptor() {

		ContainerClassDescriptor parent = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(LifecycleContextAnnotationsTests.class)
																						  .build();
		ContainerClassDescriptor containerDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder
																					  .forClass(LifecycleContextAnnotationsTests.InnerTests.class)
																					  .build(parent);
		DefaultContainerLifecycleContext lifecycleContext = new DefaultContainerLifecycleContext(
			containerDescriptor,
			reporter,
			resolveParameter
		);

		assertThat(lifecycleContext.findAnnotationsInContainer(MyAnnotation.class))
			.hasSize(1);
	}

	@Example
	void propertyLifecycleDescriptor() {

		PropertyMethodDescriptor methodDescriptor = (PropertyMethodDescriptor) TestDescriptorBuilder.forMethod(
			LifecycleContextAnnotationsTests.class, "propertyLifecycleDescriptor"
		).build();
		DefaultPropertyLifecycleContext lifecycleContext = new DefaultPropertyLifecycleContext(
			methodDescriptor,
			new LifecycleContextAnnotationsTests(),
			reporter,
			resolveParameter
		);

		assertThat(lifecycleContext.findAnnotation(Example.class)).isPresent();
		assertThat(lifecycleContext.findAnnotationsInContainer(MyAnnotation.class))
			.hasSize(1);

	}

	@Group
	private class InnerTests {
		@Property
		void aProperty(@ForAll String aString) {}
	}

}

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {
}
