package net.jqwik.time.api.dates.period;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validPeriodIsGenerated(@ForAll Period period) {
		assertThat(period).isNotNull();
	}

}
