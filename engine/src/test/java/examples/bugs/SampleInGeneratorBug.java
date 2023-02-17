package examples.bugs;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.*;

import net.jqwik.api.*;

/**
 * see https://github.com/jqwik-team/jqwik/issues/205
 */
class SampleInGeneratorBug {

	@Property(tries = 10)
	void doNotThrowConcurrentModificationException() {
		Assertions.assertDoesNotThrow(
			() -> Arbitraries.strings()
							 .map(it -> it + Arbitraries.strings().alpha().sample())
							 .filter(StringUtils::isNotBlank)
							 .sample()
		);
	}
}
