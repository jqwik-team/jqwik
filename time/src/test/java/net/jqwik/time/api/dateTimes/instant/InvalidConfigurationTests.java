package net.jqwik.time.api.dateTimes.instant;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Example
	void atTheEarliestYear1000000000() {
		assertThatThrownBy(
			() -> DateTimes.instants().atTheEarliest(Instant.MAX)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> DateTimes.instants()
						   .atTheEarliest(LocalDateTime.of(Year.MAX_VALUE, Month.DECEMBER, 31, 23, 59, 59).toInstant(ZoneOffset.MIN))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheLatestYear1000000000() {
		assertThatThrownBy(
			() -> DateTimes.instants().atTheLatest(Instant.MAX)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> DateTimes.instants()
						   .atTheLatest(LocalDateTime.of(Year.MAX_VALUE, Month.DECEMBER, 31, 23, 59, 59).toInstant(ZoneOffset.MIN))
		).isInstanceOf(IllegalArgumentException.class);
	}

}
