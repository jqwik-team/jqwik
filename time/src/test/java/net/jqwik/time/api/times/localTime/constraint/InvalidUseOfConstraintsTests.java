package net.jqwik.time.api.times.localTime.constraint;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class InvalidUseOfConstraintsTests {

	@Property
	void timeRange(@ForAll @TimeRange(min = "01:32:21.113943") Byte b) {
		assertThat(b).isNotNull();
	}

	@Property
	void hourRange(@ForAll @HourRange(min = 11, max = 13) Integer i) {
		assertThat(i).isNotNull();
	}

	@Property
	void minuteRange(@ForAll @MinuteRange(min = 11, max = 13) JqwikRandom random) {
		assertThat(random).isNotNull();
	}

	@Property
	void secondRange(@ForAll @SecondRange(min = 11, max = 13) Boolean b) {
		assertThat(b).isNotNull();
	}

	@Property
	void precision(@ForAll @Precision(value = HOURS) char c) {
		assertThat(c).isNotNull();
	}

}
