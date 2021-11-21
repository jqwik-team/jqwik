package net.jqwik.time.api.times.localTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Property
	void validLocalTimeIsGenerated(@ForAll("times") LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Property
	void worstCaseTimeGeneration4NanosPossible(@ForAll("worstCase4NanosPossible") LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Provide
	Arbitrary<LocalTime> worstCase4NanosPossible() {
		return Times.times().between(LocalTime.of(22, 59, 59, 999_999_998), LocalTime.of(23, 0, 0, 1));
	}

	@Property
	void worstCaseTimeGeneration2Minutes2SecondsPossible(@ForAll("worstCase2Minutes2SecondsPossible") LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Provide
	Arbitrary<LocalTime> worstCase2Minutes2SecondsPossible() {
		return Times.times().minuteBetween(0, 1).secondBetween(0, 1);
	}

	@Property
	void validTimeZoneIsGenerated(@ForAll("timeZones") TimeZone timeZone) {
		assertThat(timeZone).isNotNull();
	}

	@Provide
	Arbitrary<TimeZone> timeZones() {
		return Times.timeZones();
	}

	@Property
	void validZoneIdIsGenerated(@ForAll("zoneIds") ZoneId zoneId) {
		assertThat(zoneId).isNotNull();
	}

	@Provide
	Arbitrary<ZoneId> zoneIds() {
		return Times.zoneIds();
	}

}
