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
public class DefaultOffsetDateTimeArbitrary extends ArbitraryDecorator<OffsetDateTime> implements OffsetDateTimeArbitrary {

	private LocalDateTimeArbitrary localDateTimes = DateTimes.dateTimes();
	private ZoneOffsetArbitrary zoneOffsets = Times.zoneOffsets();

	@Override
	protected Arbitrary<OffsetDateTime> arbitrary() {
		return Combinators.combine(localDateTimes, zoneOffsets).as(OffsetDateTime::of);
	}

	@Override
	public OffsetDateTimeArbitrary atTheEarliest(LocalDateTime min) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.atTheEarliest(min);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary atTheLatest(LocalDateTime max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.atTheLatest(max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary dateBetween(LocalDate min, LocalDate max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.dateBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary yearBetween(Year min, Year max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.yearBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary monthBetween(Month min, Month max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.monthBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary onlyMonths(Month... months) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.onlyMonths(months);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary dayOfMonthBetween(int min, int max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.dayOfMonthBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.onlyDaysOfWeek(daysOfWeek);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary timeBetween(LocalTime min, LocalTime max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.timeBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary hourBetween(int min, int max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.hourBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary minuteBetween(int min, int max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.minuteBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary secondBetween(int min, int max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.secondBetween(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary offsetBetween(ZoneOffset min, ZoneOffset max) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.zoneOffsets = clone.zoneOffsets.between(min, max);
		return clone;
	}

	@Override
	public OffsetDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultOffsetDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.ofPrecision(ofPrecision);
		return clone;
	}
}
