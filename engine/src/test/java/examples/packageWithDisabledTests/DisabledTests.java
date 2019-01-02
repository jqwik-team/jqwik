package examples.packageWithDisabledTests;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class DisabledTests {

	@Example
	@Disabled("a reason")
	void disabledSuccess() {

	}

	@Example
	@Disabled
	void disabledFailure(@ForAll String test) {
		Assertions.fail("should fail");
	}

	@Example
	void success() {

	}

	@Group
	@Disabled
	public class DisabledGroup {

		@Example
		void successInGroup() {
		}

		@Example
		void failureInGroup() {
			Assertions.fail("should fail");
		}

	}
}
