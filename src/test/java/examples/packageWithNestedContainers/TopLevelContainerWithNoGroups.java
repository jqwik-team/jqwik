package examples.packageWithNestedContainers;

import net.jqwik.api.Example;
import net.jqwik.api.Group;

@Group // TopLevel Group annotation is being ignored
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
