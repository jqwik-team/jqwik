package examples.packageWithNestedContainers;

import net.jqwik.api.Example;

public class TopLevelContainerWithNoGroups {

	@Example
	void example1() {

	}

	public static class NestedContainer {
		@Example
		void example2() {

		}
	}
}
