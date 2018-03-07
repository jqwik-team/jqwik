package net.jqwik.discovery;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;

import java.util.*;

@Label("Container Resolver Tests")
class ContainerResolverTest {

	private TestDescriptor engineDescriptor = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

	@Group
	@Label("Top Level Containers")
	class TopLevelContainerTests {
		private TopLevelContainerResolver resolver = new TopLevelContainerResolver();

		@Example
		void resolveTopLevelContainerWithoutLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithoutLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName())
					  .isEqualTo("ContainerResolverTest.ContainerWithoutLabel");
		}

		@Example
		void resolveTopLevelContainerWithLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName())
					  .isEqualTo("container with label");
		}

	}

	@Group
	@Label("Group Containers")
	class GroupContainerTests {
		private GroupContainerResolver resolver = new GroupContainerResolver();

		@Example
		void resolveGroupContainerWithoutLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithoutLabel.GroupWithoutLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName()).isEqualTo("GroupWithoutLabel");
		}

		@Example
		void resolveGroupContainerWithLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithLabel.GroupWithLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName()).isEqualTo("group with label");
		}

	}

	static class ContainerWithoutLabel {
		@Group
		class GroupWithoutLabel {
		}
	}

	@Label("container with label")
	static class ContainerWithLabel {
		@Group
		@Label("group with label")
		class GroupWithLabel {
		}
	}

}
