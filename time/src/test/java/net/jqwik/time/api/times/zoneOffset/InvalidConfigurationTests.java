package net.jqwik.time.api.times.zoneOffset;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Example
	void minOffsetLessThanMinimumOffset() {
		assertThatThrownBy(
			() -> Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(-12, 0, -1), ZoneOffset.ofHours(1))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minOffsetGreaterThanMaximumOffset() {
		assertThatThrownBy(
			() -> Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(14, 0, 1), ZoneOffset.ofHours(15))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxOffsetGreaterThanMaximumOffset() {
		assertThatThrownBy(
			() -> Times.zoneOffsets().between(ZoneOffset.ofHours(1), ZoneOffset.ofHoursMinutesSeconds(14, 0, 1))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void noValuesCanBeGeneratedNegative() {
		assertThatThrownBy(
			() -> Times.zoneOffsets()
					   .between(ZoneOffset.ofHoursMinutesSeconds(-11, -9, -4), ZoneOffset.ofHoursMinutesSeconds(-11, -9, -3))
					   .generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void noValuesCanBeGeneratedPositive() {
		assertThatThrownBy(
			() -> Times.zoneOffsets()
					   .between(ZoneOffset.ofHoursMinutesSeconds(11, 9, 3), ZoneOffset.ofHoursMinutesSeconds(11, 9, 4))
					   .generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

}
