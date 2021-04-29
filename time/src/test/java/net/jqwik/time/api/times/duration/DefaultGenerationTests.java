package net.jqwik.time.api.times.duration;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validDurationIsGenerated(@ForAll Duration duration) {
		assertThat(duration).isNotNull();
	}

}
