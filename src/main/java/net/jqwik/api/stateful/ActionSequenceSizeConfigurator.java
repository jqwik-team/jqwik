package net.jqwik.api.stateful;

import net.jqwik.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class ActionSequenceSizeConfigurator extends ArbitraryConfiguratorBase {

	public ActionSequenceArbitrary<?> configure(ActionSequenceArbitrary<?> arbitrary, Size size) {

		checkSize(size);
		return arbitrary.ofSize(size.value());
	}

	private void checkSize(Size size) {
		if (size.value() == 0) {
			reportError(size);
		}

		if (size.min() != 0 || size.max() != 0) {
			reportError(size);
		}
	}

	private void reportError(Size size) {
		throw new JqwikException(String.format("%s: For ActionSequence values you have to choose a fixed value", size));
	}

}
