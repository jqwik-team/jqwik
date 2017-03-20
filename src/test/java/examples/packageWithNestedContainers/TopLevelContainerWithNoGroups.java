package examples.packageWithNestedContainers;

import experiments.Group;
import net.jqwik.api.Example;

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
