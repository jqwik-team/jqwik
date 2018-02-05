package examples.bugs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;

class GeneratingHighDoubles {

	@Property(tries = 100, reporting = Reporting.GENERATED)
	void shouldGenerateEquallyDistributedValues(@ForAll @Positive double aDouble) {
	}
}
