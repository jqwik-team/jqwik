package net.jqwik.time.api.dateTimes.instant;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validInstantIsGenerated(@ForAll Instant instant) {
		assertThat(instant).isNotNull();
	}

}
