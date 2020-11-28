import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

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

