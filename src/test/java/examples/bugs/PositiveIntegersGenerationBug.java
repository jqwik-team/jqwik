package examples.bugs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class PositiveIntegersGenerationBug {

	@Property
	void shouldNotCrash(@ForAll @Positive int anInt) {
		String range = anInt < 10 ? "small" : (anInt < 100 ? "middle" : "large");
		Statistics.collect(range);
	}

}
