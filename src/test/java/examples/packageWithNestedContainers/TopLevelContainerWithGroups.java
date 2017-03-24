package examples.packageWithNestedContainers;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;

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
