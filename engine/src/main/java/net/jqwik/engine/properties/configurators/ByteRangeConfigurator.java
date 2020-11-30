package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class ByteRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Byte.class);
	}

	public Arbitrary<Byte> configure(Arbitrary<Byte> arbitrary, ByteRange range) {
		if (arbitrary instanceof ByteArbitrary) {
			ByteArbitrary byteArbitrary = (ByteArbitrary) arbitrary;
			return byteArbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
