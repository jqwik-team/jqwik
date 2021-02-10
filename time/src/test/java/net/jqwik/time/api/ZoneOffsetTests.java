package net.jqwik.time.api;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
class ZoneOffsetTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validLocalTimeIsGenerated(@ForAll("offsets") ZoneOffset offset) {
			assertThat(offset).isNotNull();
		}

	}

	@Property
	@Disabled("Not available at the moment")
	void validLocalTimeIsGeneratedWithAnnotation(@ForAll ZoneOffset offset) {
		assertThat(offset).isNotNull();
	}

}
