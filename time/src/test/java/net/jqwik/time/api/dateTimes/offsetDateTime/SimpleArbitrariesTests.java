package net.jqwik.time.api.dateTimes.offsetDateTime;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<OffsetDateTime> offsetDateTimes() {
		return DateTimes.offsetDateTimes();
	}

	@Property
	void validOffsetDateTimeIsGenerated(@ForAll("offsetDateTimes") OffsetDateTime offsetDateTime) {
		assertThat(offsetDateTime).isNotNull();
	}

}
