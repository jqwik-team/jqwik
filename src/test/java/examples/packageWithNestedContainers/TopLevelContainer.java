package examples.packageWithNestedContainers;

import net.jqwik.api.Group;
import net.jqwik.api.Example;
import net.jqwik.api.Property;

@Group
public class TopLevelContainer {

	static class InnerContainer {
		@Example
		void innerExample() {
		}
	}

	static class AnotherInnerContainer {
		@Property
		boolean innerProperty() {
			return true;
		}
	}

	@Group
	public static class InnerGroup {

		public static class InnerInnerContainer {
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
