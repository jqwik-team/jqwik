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
public class DefaultZonedDateTimeArbitrary extends ArbitraryDecorator<ZonedDateTime> implements ZonedDateTimeArbitrary {

	private LocalDateTimeArbitrary localDateTimes = DateTimes.dateTimes();
	private Arbitrary<ZoneId> zoneIds = Times.zoneIds();

	public final static ZoneId ZONE_ID_IDL = ZoneId.of("Pacific/Kiritimati");
	public final static ZoneId ZONE_ID_ZERO = ZoneId.of("Europe/London");
	public final static ZoneId ZONE_ID_IDLW = ZoneId.of("Etc/GMT+12");

	@Override
	protected Arbitrary<ZonedDateTime> arbitrary() {
		zoneIds = zoneIds.edgeCases(config -> {
			config.add(ZONE_ID_IDL, ZONE_ID_ZERO, ZONE_ID_IDLW);
			config.filter(this::edgeCaseFilter);
		});
		return Combinators.combine(localDateTimes, zoneIds)
						  .as(this::getStrict)
						  .ignoreException(DateTimeException.class);
	}

	private ZonedDateTime getStrict(LocalDateTime dateTime, ZoneId zoneId) {
		return ZonedDateTime.ofStrict(dateTime, zoneId.getRules().getOffset(dateTime), zoneId);
	}

	private boolean edgeCaseFilter(ZoneId zoneId) {
		return zoneId.equals(ZONE_ID_IDL) || zoneId.equals(ZONE_ID_ZERO) || zoneId.equals(ZONE_ID_IDLW);
	}

	@Override
	public ZonedDateTimeArbitrary atTheEarliest(LocalDateTime min) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.atTheEarliest(min);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary atTheLatest(LocalDateTime max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.atTheLatest(max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary dateBetween(LocalDate min, LocalDate max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.dateBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary yearBetween(Year min, Year max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.yearBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary monthBetween(Month min, Month max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.monthBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary onlyMonths(Month... months) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.onlyMonths(months);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary dayOfMonthBetween(int min, int max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.dayOfMonthBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.onlyDaysOfWeek(daysOfWeek);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary timeBetween(LocalTime min, LocalTime max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.timeBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary hourBetween(int min, int max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.hourBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary minuteBetween(int min, int max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.minuteBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary secondBetween(int min, int max) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.secondBetween(min, max);
		return clone;
	}

	@Override
	public ZonedDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultZonedDateTimeArbitrary clone = typedClone();
		clone.localDateTimes = clone.localDateTimes.ofPrecision(ofPrecision);
		return clone;
	}

}
