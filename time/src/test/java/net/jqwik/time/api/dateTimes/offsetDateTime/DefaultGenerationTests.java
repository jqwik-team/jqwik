package net.jqwik.time.api.dateTimes.offsetDateTime;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validOffsetDateTimeIsGenerated(@ForAll OffsetDateTime dateTime) {
		assertThat(dateTime).isNotNull();
	}

}
