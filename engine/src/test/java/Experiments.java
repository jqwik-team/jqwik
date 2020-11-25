import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class Experiments {

	@Property
	void failOnNull(@ForAll("uuid") String uuid) {
		Assertions.assertThat(uuid).isNull();
	}

	@Provide
	Arbitrary<String> uuid() {
		return Arbitraries.create(() -> UUID.randomUUID().toString());
	}
}

