package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;
import org.assertj.core.api.*;
import org.assertj.core.data.*;

class ScaleProperties {
	@Property
	void floatsWithScale(@ForAll @FloatRange(max = 1000.0f) @Scale(1) float value) {
		Assertions.assertThat(value * 10).isCloseTo(Math.round(value * 10.0), Offset.offset(0.05f));
	}

	@Property
	void doublesWithScale(@ForAll @DoubleRange(max = 1000.0) @Scale(1) double value) {
		Assertions.assertThat(value * 10).isCloseTo(Math.round(value * 10.0), Offset.offset(0.05));
	}

	@Property
	boolean bigDecimalsWithScale(@ForAll @BigRange(max = "1000") @Scale(1) BigDecimal value) {
		return value.scale() <= 1;
	}
}
