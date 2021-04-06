package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class ZoneOffsetBetween extends ValueRange<ZoneOffset> {
	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().getTotalSeconds() > parameter.getMax().getTotalSeconds()) {
			parameter.changeMinMax();
		}
	}
}
