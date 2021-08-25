package examples.packageWithProperties;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class TestsForReporting {

	@Example
	void succeeding() {
	}

	@Property(tries = 2)
	void succeedingPropertyWithoutParameters() {
	}

	@Example
	void succeedingWithForAll(@ForAll String aString) {
	}

	@Example
	void failing() {
		fail("failing");
	}
}
