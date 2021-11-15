package net.jqwik.time.api.dateTimes.zonedDateTime;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validOffsetDateTimeIsGenerated(@ForAll ZonedDateTime dateTime) {
		assertThat(dateTime).isNotNull();
	}

}
