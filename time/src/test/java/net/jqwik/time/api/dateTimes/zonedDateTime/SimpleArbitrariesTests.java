package net.jqwik.time.api.dateTimes.zonedDateTime;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<ZonedDateTime> zonedDateTimes() {
		return DateTimes.zonedDateTime();
	}

	@Property
	void validZonedDateTimeIsGenerated(@ForAll("zonedDateTimes") ZonedDateTime zonedDateTime) {
		assertThat(zonedDateTime).isNotNull();
	}

}
