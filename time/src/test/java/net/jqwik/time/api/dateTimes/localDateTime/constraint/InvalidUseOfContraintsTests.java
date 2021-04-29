package net.jqwik.time.api.dateTimes.localDateTime.constraint;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

public class InvalidUseOfContraintsTests {

	@Property
	void dateTimeRange(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") String string) {
		assertThat(string).isNotNull();
	}

	@Property
	void precision(@ForAll @Precision(value = HOURS) Byte b) {
		assertThat(b).isNotNull();
	}

}
