package net.jqwik.time.api.dateTimes.localDateTime;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validLocalDateTimeIsGenerated(@ForAll LocalDateTime dateTime) {
		assertThat(dateTime).isNotNull();
	}

}
