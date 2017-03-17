package examples.packageWithNestedContainers;

import net.jqwik.api.Group;
import net.jqwik.api.Example;
import net.jqwik.api.Property;

public class TopLevelContainerWithGroups {

	@Group
	static class InnerContainer {
		@Example
		void innerExample() {
		}
	}

	@Group
	static class AnotherInnerContainer {
		@Property
		boolean innerProperty() {
			return true;
		}
	}

	@Group
	public static class InnerGroup {

		@Group
		public static class InnerInnerGroup {
			@Property
			boolean innerInnerProperty() {
				return true;
			}
		}

	}

	@Example
	void topLevelExample() {
	}
}
