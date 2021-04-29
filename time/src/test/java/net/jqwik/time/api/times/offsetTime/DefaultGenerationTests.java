package net.jqwik.time.api.times.offsetTime;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validOffsetTimeIsGenerated(@ForAll OffsetTime time) {
		assertThat(time).isNotNull();
	}

}
