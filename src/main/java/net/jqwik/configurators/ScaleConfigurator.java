package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class ScaleConfigurator extends ArbitraryConfiguratorBase {

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, Scale scale) {
		return arbitrary.withScale(scale.value());
	}

	public DoubleArbitrary configure(DoubleArbitrary arbitrary, Scale scale) {
		return arbitrary.withScale(scale.value());
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, Scale scale) {
		return arbitrary.withScale(scale.value());
	}

}
