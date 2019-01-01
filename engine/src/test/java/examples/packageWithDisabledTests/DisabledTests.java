package examples.packageWithDisabledTests;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;

public class DisabledTests {

	@Example
	@Disabled
	void disabledSuccess() {

	}
}
