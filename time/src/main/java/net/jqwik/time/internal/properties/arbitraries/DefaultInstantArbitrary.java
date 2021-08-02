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
public class DefaultInstantArbitrary extends ArbitraryDecorator<Instant> implements InstantArbitrary {

	private LocalDateTimeArbitrary dateTimeArbitrary;

	public DefaultInstantArbitrary() {
		dateTimeArbitrary = DateTimes.dateTimes();
	}

	@Override
	protected Arbitrary<Instant> arbitrary() {

		return dateTimeArbitrary.map(dateTime -> dateTime.toInstant(ZoneOffset.UTC));

	}

	private LocalDateTime instantToLocalDateTime(Instant instant) {
		if (instant.isAfter(LocalDateTime.MAX.toInstant(ZoneOffset.UTC))) {
			throw new IllegalArgumentException("Maximum supported year is 999999999 (Year.MAX_VALUE).");
		}
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	@Override
	public InstantArbitrary atTheEarliest(Instant min) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.atTheEarliest(instantToLocalDateTime(min));
		return clone;
	}

	@Override
	public InstantArbitrary atTheLatest(Instant max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.atTheLatest(instantToLocalDateTime(max));
		return clone;
	}

	@Override
	public InstantArbitrary dateBetween(LocalDate min, LocalDate max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.dateBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary yearBetween(Year min, Year max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.yearBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary monthBetween(Month min, Month max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.monthBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary onlyMonths(Month... months) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.onlyMonths(months);
		return clone;
	}

	@Override
	public InstantArbitrary dayOfMonthBetween(int min, int max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.dayOfMonthBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.onlyDaysOfWeek(daysOfWeek);
		return clone;
	}

	@Override
	public InstantArbitrary timeBetween(LocalTime min, LocalTime max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.timeBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary hourBetween(int min, int max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.hourBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary minuteBetween(int min, int max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.minuteBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary secondBetween(int min, int max) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.secondBetween(min, max);
		return clone;
	}

	@Override
	public InstantArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultInstantArbitrary clone = typedClone();
		clone.dateTimeArbitrary = clone.dateTimeArbitrary.ofPrecision(ofPrecision);
		return clone;
	}
}
