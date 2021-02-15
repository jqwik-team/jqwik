package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	public static final ZoneOffset DEFAULT_MIN = ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0);
	public static final ZoneOffset DEFAULT_MAX = ZoneOffset.ofHoursMinutesSeconds(14, 0, 0);

	private ZoneOffset offsetMin = DEFAULT_MIN;
	private ZoneOffset offsetMax = DEFAULT_MAX;

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		ZoneOffset[] zoneOffsets = generateAllValues();

		int indexZero = Integer.MIN_VALUE, indexMin = zoneOffsets.length - 1, indexMax = zoneOffsets.length - 1;
		boolean indexZeroSet = false, indexMinSet = false, indexMaxSet = false;

		for (int i = 0; i < zoneOffsets.length; i++) {
			if (!indexZeroSet && zoneOffsets[i].getTotalSeconds() == 0) {
				indexZeroSet = true;
				indexZero = i;
			}
			if (!indexMinSet && zoneOffsets[i].getTotalSeconds() >= offsetMin.getTotalSeconds()) {
				indexMin = i;
				indexMinSet = true;
			}
			if (!indexMaxSet && zoneOffsets[i].getTotalSeconds() > offsetMax.getTotalSeconds()) {
				indexMax = i - 1;
				indexMaxSet = true;
			}
		}

		int min = indexMin - indexZero;
		int max = indexMax - indexZero;

		Arbitrary<Integer> indexes = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(min, max)
												.edgeCases(edgeCases -> edgeCases.includeOnly(min, 0, max));

		final int toAdd = indexZero;

		return indexes.map(i -> zoneOffsets[i + toAdd]);

	}

	private ZoneOffset[] generateAllValues() {
		ArrayList<ZoneOffset> offsets = new ArrayList<ZoneOffset>();
		offsets.add(ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0));
		for (int h = -11; h <= 0; h++) {
			for (int m = -45; m <= 0; m += 15) {
				offsets.add(ZoneOffset.ofHoursMinutesSeconds(h, m, 0));
			}
		}
		boolean first = true;
		for (int h = 0; h <= 13; h++) {
			for (int m = 0; m <= 45; m += 15) {
				if (first) {
					first = false;
					continue;
				}
				offsets.add(ZoneOffset.ofHoursMinutesSeconds(h, m, 0));
			}
		}
		offsets.add(ZoneOffset.ofHoursMinutesSeconds(14, 0, 0));
		return offsets.toArray(new ZoneOffset[]{});
	}

	@Override
	public ZoneOffsetArbitrary atTheEarliest(ZoneOffset min) {
		if (min.getTotalSeconds() < DEFAULT_MIN.getTotalSeconds() || min.getTotalSeconds() > DEFAULT_MAX.getTotalSeconds()) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		if ((offsetMax != null) && min.getTotalSeconds() > offsetMax.getTotalSeconds()) {
			throw new IllegalArgumentException("Minimum offset must not be after maximum offset");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMin = min;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary atTheLatest(ZoneOffset max) {
		if (max.getTotalSeconds() < DEFAULT_MIN.getTotalSeconds() || max.getTotalSeconds() > DEFAULT_MAX.getTotalSeconds()) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		if ((offsetMin != null) && max.getTotalSeconds() < offsetMin.getTotalSeconds()) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMax = max;
		return clone;
	}

}
