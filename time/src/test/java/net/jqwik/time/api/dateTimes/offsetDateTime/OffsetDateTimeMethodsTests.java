package net.jqwik.time.api.dateTimes.offsetDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class OffsetDateTimeMethodsTests {

	@Provide
	Arbitrary<Integer> years() {
		return Arbitraries.integers().between(1, LocalDateTime.MAX.getYear());
	}

	@Provide
	Arbitrary<Integer> months() {
		return Arbitraries.integers().between(1, 12);
	}

	@Provide
	Arbitrary<Integer> dayOfMonths() {
		return Arbitraries.integers().between(1, 31);
	}

	@Provide
	Arbitrary<Integer> seconds() {
		return Arbitraries.integers().between(0, 59);
	}

	@Provide
	Arbitrary<Integer> hours() {
		return Arbitraries.integers().between(0, 23);
	}

	@Provide
	Arbitrary<Integer> minutes() {
		return Arbitraries.integers().between(0, 59);
	}

	@Provide
	Arbitrary<OffsetDateTime> precisionHours() {
		return DateTimes.offsetDateTimes().ofPrecision(HOURS);
	}

	@Property
	void atTheEarliest(@ForAll LocalDateTime min, @ForAll JqwikRandom random) {

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().atTheEarliest(min);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.toLocalDateTime()).isAfterOrEqualTo(min);
			return true;
		});

	}

	@Property
	void atTheLatest(@ForAll LocalDateTime max, @ForAll JqwikRandom random) {

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().atTheLatest(max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.toLocalDateTime()).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void between(@ForAll LocalDateTime min, @ForAll LocalDateTime max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().between(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.toLocalDateTime()).isAfterOrEqualTo(min);
			assertThat(dateTime.toLocalDateTime()).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void betweenMinAfterMax(@ForAll LocalDateTime min, @ForAll LocalDateTime max, @ForAll JqwikRandom random) {

		Assume.that(min.isAfter(max));

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().between(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.toLocalDateTime()).isAfterOrEqualTo(max);
			assertThat(dateTime.toLocalDateTime()).isBeforeOrEqualTo(min);
			return true;
		});

	}

	@Property
	void dateBetween(@ForAll LocalDate min, @ForAll LocalDate max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().dateBetween(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(min);
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void yearBetween(@ForAll("years") int min, @ForAll("years") int max, @ForAll JqwikRandom random) {

		Assume.that(min <= max);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().yearBetween(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(min);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(max);
			return true;
		});

	}

	@Property
	void monthBetween(@ForAll("months") int min, @ForAll("months") int max, @ForAll JqwikRandom random) {

		Assume.that(min <= max);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().monthBetween(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.of(min));
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.of(max));
			return true;
		});

	}

	@Property
	void onlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().onlyMonths(months.toArray(new Month[]{}));

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.getMonth()).isIn(months);
			return true;
		});

	}

	@Property
	void dayOfMonthBetween(
		@ForAll("dayOfMonths") int min,
		@ForAll("dayOfMonths") int max,
		@ForAll JqwikRandom random
	) {

		Assume.that(min <= max);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().dayOfMonthBetween(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(min);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(max);
			return true;
		});

	}

	@Property
	void onlyDaysOfWeek(@ForAll @Size(min = 1) Set<DayOfWeek> dayOfWeeks, @ForAll JqwikRandom random) {

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime.getDayOfWeek()).isIn(dayOfWeeks);
			return true;
		});
	}

	@Property
	void timeBetween(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().timeBetween(min, max);

		checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
			assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(min);
			assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(max);
			return true;
		});
	}

	@Property
	void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll JqwikRandom random) {

		Assume.that(startHour <= endHour);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().hourBetween(startHour, endHour);

		checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
			assertThat(dateTime.getHour()).isGreaterThanOrEqualTo(startHour);
			assertThat(dateTime.getHour()).isLessThanOrEqualTo(endHour);
			return true;
		});

	}

	@Property
	void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll JqwikRandom random) {

		Assume.that(startMinute <= endMinute);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().minuteBetween(startMinute, endMinute);

		checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
			assertThat(dateTime.getMinute()).isGreaterThanOrEqualTo(startMinute);
			assertThat(dateTime.getMinute()).isLessThanOrEqualTo(endMinute);
			return true;
		});

	}

	@Property
	void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll JqwikRandom random) {

		Assume.that(startSecond <= endSecond);

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().secondBetween(startSecond, endSecond);

		checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
			assertThat(dateTime.getSecond()).isGreaterThanOrEqualTo(startSecond);
			assertThat(dateTime.getSecond()).isLessThanOrEqualTo(endSecond);
			return true;
		});

	}

	@Property
	void hours(@ForAll("precisionHours") OffsetDateTime dateTime) {
		assertThat(dateTime.getMinute()).isEqualTo(0);
		assertThat(dateTime.getSecond()).isEqualTo(0);
		assertThat(dateTime.getNano()).isEqualTo(0);
	}

	@Property
	void offsetBetween(@ForAll ZoneOffset startOffset, @ForAll ZoneOffset endOffset, @ForAll JqwikRandom random) {

		Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

		Arbitrary<OffsetDateTime> dateTimes = DateTimes.offsetDateTimes().offsetBetween(startOffset, endOffset);

		checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
			assertThat(dateTime.getOffset().getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
			assertThat(dateTime.getOffset().getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
			return true;
		});

	}

}
