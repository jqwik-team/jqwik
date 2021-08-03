package net.jqwik.time.api.dateTimes.instant.constraint;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidUseOfContraintsTests {

	@Property
	void instantRange(@ForAll @InstantRange(min = "2013-05-25T01:32:21.113943Z", max = "2020-08-23T01:32:21.113943Z") Integer i) {
		assertThat(i).isNotNull();
	}

}
