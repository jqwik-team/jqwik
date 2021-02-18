package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultOffsetTimeArbitrary extends ArbitraryDecorator<OffsetTime> implements OffsetTimeArbitrary {

	private LocalTimeArbitrary localTimes;
	private ZoneOffsetArbitrary zoneOffsets;

	public DefaultOffsetTimeArbitrary() {
		localTimes = Times.times();
		zoneOffsets = Times.zoneOffsets();
	}

	@Override
	protected Arbitrary<OffsetTime> arbitrary() {
		return Combinators.combine(localTimes, zoneOffsets).as(OffsetTime::of);
	}

	@Override
	public OffsetTimeArbitrary atTheEarliest(LocalTime min) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.atTheEarliest(min);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary atTheLatest(LocalTime max) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.atTheLatest(max);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary hourBetween(int min, int max) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.hourBetween(min, max);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary minuteBetween(int min, int max) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.minuteBetween(min, max);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary secondBetween(int min, int max) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.secondBetween(min, max);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary offsetBetween(ZoneOffset min, ZoneOffset max) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.zoneOffsets = clone.zoneOffsets.between(min, max);
		return clone;
	}

	@Override
	public OffsetTimeArbitrary constrainPrecision(ChronoUnit ofPrecision) {
		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.localTimes = clone.localTimes.constrainPrecision(ofPrecision);
		return clone;
	}

}
