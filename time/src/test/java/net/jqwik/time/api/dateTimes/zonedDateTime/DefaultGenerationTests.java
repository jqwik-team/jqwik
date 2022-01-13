package net.jqwik.time.api.dateTimes.zonedDateTime;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@PropertyDefaults(tries = 100)
public class DefaultGenerationTests {

	@Property
	void validOffsetDateTimeIsGenerated(@ForAll ZonedDateTime dateTime) {
		assertThat(dateTime).isNotNull();
	}

}
