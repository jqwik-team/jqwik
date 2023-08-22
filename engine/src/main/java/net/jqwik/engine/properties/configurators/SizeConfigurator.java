package net.jqwik.engine.properties.configurators;

import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;

public class SizeConfigurator extends ArbitraryConfiguratorBase {

	private static final Logger LOG = Logger.getLogger(SizeConfigurator.class.getName());

	public SizableArbitrary<?> configure(SizableArbitrary<?> arbitrary, Size size) {
		checkSize(size);
		if (size.value() != 0) {
			return arbitrary.ofSize(size.value());
		} else {
			SizableArbitrary<?> newArbitrary = arbitrary;
			if (size.min() != 0) {
				newArbitrary = newArbitrary.ofMinSize(size.min());
			}
			if (size.max() != 0) {
				newArbitrary = newArbitrary.ofMaxSize(size.max());
			}
			return newArbitrary;
		}
	}

	public ActionSequenceArbitrary<?> configure(ActionSequenceArbitrary<?> arbitrary, Size size) {
		int effectiveSize = Math.max(size.value(), Math.max(size.max(), size.min()));
		if (size.value() <= 0 || size.min() != 0 || size.max() != 0) {
			String message = String.format(
				"%s:" +
					"%n    You have to choose just a fixed positive value for size of action sequence." +
					"%n    Use @Size(%s) instead." +
					"%n    This usage will throw exception starting with version 1.7.0.",
				size, effectiveSize
			);
			throw new JqwikException(message);
		}
		return arbitrary.ofSize(effectiveSize);
	}

	private void checkSize(Size size) {
		if (size.value() == 0) {
			if (size.min() > size.max() && size.max() != 0) {
				reportError(size);
			}
		} else {
			if (size.min() != 0 || size.max() != 0)
				reportError(size);
		}
	}

	private void reportError(Size size) {
		throw new JqwikException(String.format("%s: You have to either choose a fixed value or set min and max", size));
	}

}
