package net.jqwik.time.api.times.offsetTime;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<OffsetTime> times() {
		return Times.offsetTimes();
	}

	@Property
	void validOffsetTimeIsGenerated(@ForAll("times") OffsetTime time) {
		assertThat(time).isNotNull();
	}

}
