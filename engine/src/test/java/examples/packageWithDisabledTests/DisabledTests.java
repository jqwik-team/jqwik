package examples.packageWithDisabledTests;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class DisabledTests {

	@Example
	@Disabled
	void disabledSuccess() {

	}

	@Example
	@Disabled
	void disabledFailure() {
		Assertions.fail("should fail");
	}

	@Example
	void success() {

	}

	@Group
	@Disabled
	class DisabledGroup {

		@Example
		void successInGroup() {
		}

		@Example
		void failureInGroup() {
			Assertions.fail("should fail");
		}

	}
}
