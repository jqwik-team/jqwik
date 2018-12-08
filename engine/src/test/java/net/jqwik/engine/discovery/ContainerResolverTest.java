package net.jqwik.engine.discovery;

import java.util.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;

@Label("Container Resolver Tests")
class ContainerResolverTest {

	private TestDescriptor engineDescriptor = TestDescriptorBuilder.forEngine(new JqwikTestEngine()).build();

	@Group
	@Label("Top Level Containers")
	class TopLevelContainerTests {
		private TopLevelContainerResolver resolver = new TopLevelContainerResolver();

		@Example
		@Label("without label")
		void withoutLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithoutLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName())
					  .isEqualTo("ContainerResolverTest.ContainerWithoutLabel");
		}

		@Example
		@Label("with label")
		void withLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(Container_With_Label.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName())
					  .isEqualTo("container with label");
		}

		@Example
		void with_underscores() {
			Set<TestDescriptor> descriptors = resolver.resolveElement(Container_with_underscores.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName())
					  .isEqualTo("ContainerResolverTest.Container with underscores");
		}

		@Example
		@Label("with tags")
		void withTags() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithTags.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getTags())
					  .containsExactly(TestTag.create("container-tag-1"), TestTag.create("container-tag-2"));
		}

	}

	@Group
	@Label("Group Containers")
	class GroupContainerTests {
		private GroupContainerResolver resolver = new GroupContainerResolver();

		@Example
		@Label("without label")
		void withoutLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithoutLabel.GroupWithoutLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName()).isEqualTo("GroupWithoutLabel");
		}

		@Example
		@Label("with label")
		void withLabel() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(Container_With_Label.GroupWithLabel.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName()).isEqualTo("group with label");
		}

		@Example
		void with_underscores() {

			Set<TestDescriptor> descriptors = resolver
												  .resolveElement(Container_with_underscores.Group_with_underscores.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getDisplayName()).isEqualTo("Group with underscores");
		}

		@Example
		@Label("with tag")
		void withTag() {

			Set<TestDescriptor> descriptors = resolver.resolveElement(ContainerWithTags.GroupWithTag.class, engineDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			ContainerClassDescriptor classTestDescriptor = (ContainerClassDescriptor) descriptors.iterator().next();
			Assertions.assertThat(classTestDescriptor.getTags()).containsExactly(TestTag.create("group-tag"));
		}

	}

	static class ContainerWithoutLabel {
		@Group
		class GroupWithoutLabel {
		}
	}

	@Label("container with label")
	static class Container_With_Label {
		@Group
		@Label("group with label")
		class GroupWithLabel {
		}
	}

	static class Container_with_underscores {
		@Group
		class Group_with_underscores {
		}
	}

	@Tag("container-tag-1")
	@Tag("container-tag-2")
	static class ContainerWithTags {
		@Group
		@Tag("group-tag")
		class GroupWithTag {
		}
	}

}
