package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;
import org.assertj.core.api.*;
import org.assertj.core.data.*;

class ScaleProperties {
	@Property
	void floatsWithScale(@ForAll @FloatRange(max = 1000.0f) @Scale(2) float value) {
		Assertions.assertThat(value * 100).isCloseTo(Math.round(value * 100.0), Offset.offset(0.005f));
	}

	@Property
	void doublesWithScale(@ForAll @DoubleRange(max = 1000.0) @Scale(2) double value) {
		Assertions.assertThat(value * 100).isCloseTo(Math.round(value * 100.0), Offset.offset(0.005));
	}

	@Property
	boolean bigDecimalsWithScale(@ForAll @FloatRange(max = 1000) @Scale(2) BigDecimal value) {
		return value.scale() <= 2;
	}
}
