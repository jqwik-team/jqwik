package net.jqwik.time.api.times.localTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validLocalTimeIsGenerated(@ForAll LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Property
	void validZoneIdIsGenerated(@ForAll ZoneId zoneId) {
		assertThat(zoneId).isNotNull();
	}

	@Property
	void validTimeZoneIsGenerated(@ForAll TimeZone timeZone) {
		assertThat(timeZone).isNotNull();
	}

}
