package examples.packageWithNestedContainers;

import net.jqwik.api.Example;
import net.jqwik.api.Group;

public class ClassWithContainer {

	@Group // should be ignored
	public static class NestedGroupAnnotatedContainer {
		@Example
		void example2() {

		}
	}
}
