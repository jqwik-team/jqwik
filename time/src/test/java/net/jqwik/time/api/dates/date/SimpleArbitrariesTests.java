package net.jqwik.time.api.dates.date;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<Date> dates() {
		return Dates.datesAsDate();
	}

	@Property
	void validDateIsGenerated(@ForAll("dates") Date date) {
		assertThat(date).isNotNull();
	}

}
