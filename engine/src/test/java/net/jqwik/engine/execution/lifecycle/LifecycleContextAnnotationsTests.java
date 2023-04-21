package net.jqwik.engine.execution.lifecycle;

import java.lang.annotation.*;
import java.util.*;

import org.junit.platform.engine.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;

import static org.assertj.core.api.Assertions.*;

@MyAnnotation
@MyRepeatableAnnotation("one")
@MyRepeatableAnnotation("two")
class LifecycleContextAnnotationsTests {

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
		assertThat(lifecycleContext.findAnnotation(MyAnnotation.class)).isEmpty();
		assertThat(lifecycleContext.findAnnotationsInContainer(MyAnnotation.class)).isEmpty();
		assertThat(lifecycleContext.findRepeatableAnnotations(MyRepeatableAnnotation.class)).isEmpty();
	}

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

		List<MyRepeatableAnnotation> repeatableAnnotations = lifecycleContext.findRepeatableAnnotations(MyRepeatableAnnotation.class);
		assertThat(repeatableAnnotations).isNotEmpty();
		assertThat(repeatableAnnotations.stream().map(MyRepeatableAnnotation::value))
			.contains("one", "two");
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

	@MyRepeatableAnnotation("three")
	@MyRepeatableAnnotation("four")
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

		List<MyRepeatableAnnotation> repeatableAnnotations = lifecycleContext.findRepeatableAnnotations(MyRepeatableAnnotation.class);
		assertThat(repeatableAnnotations).isNotEmpty();
		assertThat(repeatableAnnotations.stream().map(MyRepeatableAnnotation::value))
			.contains("three", "four");

	}

	@MyRepeatableAnnotation("five")
	@MyRepeatableAnnotation("six")
	@Example
	void tryLifecycleDescriptor() {

		PropertyMethodDescriptor methodDescriptor = (PropertyMethodDescriptor) TestDescriptorBuilder.forMethod(
			LifecycleContextAnnotationsTests.class, "tryLifecycleDescriptor"
		).build();
		DefaultPropertyLifecycleContext propertyLifecycleContext = new DefaultPropertyLifecycleContext(
			methodDescriptor,
			new LifecycleContextAnnotationsTests(),
			reporter,
			resolveParameter
		);

		TryLifecycleContext lifecycleContext = new DefaultTryLifecycleContext(propertyLifecycleContext);

		assertThat(lifecycleContext.findAnnotation(Example.class)).isPresent();
		assertThat(lifecycleContext.findAnnotationsInContainer(MyAnnotation.class))
			.hasSize(1);

		List<MyRepeatableAnnotation> repeatableAnnotations = lifecycleContext.findRepeatableAnnotations(MyRepeatableAnnotation.class);
		assertThat(repeatableAnnotations).isNotEmpty();
		assertThat(repeatableAnnotations.stream().map(MyRepeatableAnnotation::value))
			.contains("five", "six");

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

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MyRepeatableAnnotations.class)
@interface MyRepeatableAnnotation {
	String value();
}

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface MyRepeatableAnnotations {
	MyRepeatableAnnotation[] value();
}