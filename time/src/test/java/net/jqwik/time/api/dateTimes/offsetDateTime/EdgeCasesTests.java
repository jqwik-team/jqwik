package net.jqwik.time.api.dateTimes.offsetDateTime;

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
		OffsetDateTimeArbitrary dateTimes = DateTimes.offsetDateTimes();
		Set<OffsetDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
		assertThat(edgeCases).hasSize(3 * 6);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), ZoneOffset.ofHours(14)),
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 23, 59, 59), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 23, 59, 59), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(1900, 1, 1, 23, 59, 59), ZoneOffset.ofHours(14)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 0, 0, 0), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 0, 0, 0), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 0, 0, 0), ZoneOffset.ofHours(14)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 23, 59, 59), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 23, 59, 59), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(1904, 2, 29, 23, 59, 59), ZoneOffset.ofHours(14)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 0, 0, 0), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 0, 0, 0), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 0, 0, 0), ZoneOffset.ofHours(14)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 23, 59, 59), ZoneOffset.ofHours(-12)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 23, 59, 59), ZoneOffset.ofHours(0)),
			OffsetDateTime.of(LocalDateTime.of(2500, 12, 31, 23, 59, 59), ZoneOffset.ofHours(14))
		);
	}

}
