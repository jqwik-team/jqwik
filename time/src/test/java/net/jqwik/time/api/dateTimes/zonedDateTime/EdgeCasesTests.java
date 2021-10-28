package net.jqwik.time.api.dateTimes.zonedDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
public class EdgeCasesTests {

	@Example
	void all() {
		ZonedDateTimeArbitrary dateTimes = DateTimes.zonedDateTimes();
		Set<ZonedDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
		assertThat(edgeCases).hasSize(2 * 6);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), ZoneId.of("Etc/GMT+12")),
			ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 23, 59, 59), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 23, 59, 59), ZoneId.of("Etc/GMT+12")),
			ZonedDateTime.of(LocalDateTime.of(1904, 2, 29, 0, 0, 0), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(1904, 2, 29, 0, 0, 0), ZoneId.of("Etc/GMT+12")),
			ZonedDateTime.of(LocalDateTime.of(1904, 2, 29, 23, 59, 59), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(1904, 2, 29, 23, 59, 59), ZoneId.of("Etc/GMT+12")),
			ZonedDateTime.of(LocalDateTime.of(2500, 12, 31, 0, 0, 0), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(2500, 12, 31, 0, 0, 0), ZoneId.of("Etc/GMT+12")),
			ZonedDateTime.of(LocalDateTime.of(2500, 12, 31, 23, 59, 59), ZoneId.of("Etc/GMT-14")),
			ZonedDateTime.of(LocalDateTime.of(2500, 12, 31, 23, 59, 59), ZoneId.of("Etc/GMT+12"))
		);
	}

}
