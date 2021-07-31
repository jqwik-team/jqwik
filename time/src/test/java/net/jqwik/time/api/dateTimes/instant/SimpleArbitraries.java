package net.jqwik.time.api.dateTimes.instant;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitraries {

	@Provide
	Arbitrary<Instant> instants() {
		return DateTimes.instants();
	}

	@Property
	void validInstantIsGenerated(@ForAll("instants") Instant instant) {
		assertThat(instant).isNotNull();
	}

}
