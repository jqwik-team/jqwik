package net.jqwik.time.api.times.zoneOffset;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class EdgeCasesTests {

	@Example
	void all() {
		ZoneOffsetArbitrary offsets = Times.zoneOffsets();
		Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0),
			ZoneOffset.of("Z"),
			ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
		);
	}

	@Example
	void betweenPositive() {
		ZoneOffsetArbitrary offsets =
			Times.zoneOffsets()
				 .between(ZoneOffset.ofHoursMinutesSeconds(11, 23, 21), ZoneOffset.ofHoursMinutesSeconds(13, 29, 59));
		Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			ZoneOffset.ofHoursMinutesSeconds(11, 30, 0),
			ZoneOffset.ofHoursMinutesSeconds(13, 15, 0)
		);
	}

	@Example
	void betweenNegative() {
		ZoneOffsetArbitrary offsets =
			Times.zoneOffsets()
				 .between(ZoneOffset.ofHoursMinutesSeconds(-11, -15, -19), ZoneOffset.ofHoursMinutesSeconds(-10, -23, -21));
		Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			ZoneOffset.ofHoursMinutesSeconds(-11, -15, 0),
			ZoneOffset.ofHoursMinutesSeconds(-10, -30, 0)
		);
	}

}
