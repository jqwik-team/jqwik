package net.jqwik.time.api.times.zoneOffset;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Property
	void validZoneOffsetIsGenerated(@ForAll("offsets") ZoneOffset offset) {
		assertThat(offset).isNotNull();
	}

	@Property
	void onlyValidHoursAreGenerated(@ForAll("offsets") ZoneOffset offset) {
		ZoneOffset offsetStart = ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0);
		ZoneOffset offsetEnd = ZoneOffset.ofHoursMinutesSeconds(14, 0, 0);
		assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(offsetStart.getTotalSeconds());
		assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(offsetEnd.getTotalSeconds());
	}

	@Property
	void onlyValidMinutesAreGenerated(@ForAll("offsets") ZoneOffset offset) {
		int minutes = Math.abs((offset.getTotalSeconds() % 3600) / 60);
		assertThat(minutes % 15).isEqualTo(0);
	}

	@Property
	void onlyValidSecondsAreGenerated(@ForAll("offsets") ZoneOffset offset) {
		int seconds = Math.abs(offset.getTotalSeconds() % 60);
		assertThat(seconds).isEqualTo(0);
	}

}
