package net.jqwik.time.api.times.zoneOffset;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validZoneOffsetIsGenerated(@ForAll ZoneOffset offset) {
		assertThat(offset).isNotNull();
	}

}
