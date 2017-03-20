package examples.packageWithNestedContainers;

import net.jqwik.api.Group;
import net.jqwik.api.Example;
import net.jqwik.api.properties.Property;

public class TopLevelContainerWithGroups {

	@Group
	class InnerContainer {
		@Example
		void innerExample() {
		}
	}

	@Group
	class AnotherInnerContainer {
		@Property
		boolean innerProperty() {
			return true;
		}
	}

	@Group
	public class InnerGroup {

		@Group
		public class InnerInnerGroup {
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
