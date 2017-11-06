package examples.bugs;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

class PropertyLifecyclePerCheck {

	private int counter = 0;

	@Property
	void manyChecks(@ForAll String dontCare) {
		Assertions.assertThat(counter).isEqualTo(0);
		counter++;
	}
}
