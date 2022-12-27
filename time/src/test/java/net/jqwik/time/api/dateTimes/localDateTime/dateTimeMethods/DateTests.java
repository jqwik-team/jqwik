package net.jqwik.time.api.dateTimes.localDateTime.dateTimeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@PropertyDefaults(tries = 100)
public class DateTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Group
	class DateBetweenMethod {

		@Property
		void between(@ForAll LocalDate min, @ForAll LocalDate max, @ForAll JqwikRandom random) {

			Assume.that(!min.isAfter(max));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().dateBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(min);
				assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(max);
				return true;
			});

		}

		@Property
		void betweenSame(@ForAll LocalDate same, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().dateBetween(same, same);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.toLocalDate()).isEqualTo(same);
				return true;
			});

		}

		@Property(tries = 2000, maxDiscardRatio = 10)
		void betweenDateSetWhenBetweenSet(
			@ForAll LocalDate minDate,
			@ForAll LocalDate maxDate,
			@ForAll("dateTimes") LocalDateTime min,
			@ForAll("dateTimes") LocalDateTime max,
			@ForAll JqwikRandom random
		) {

			Assume.that(!minDate.isAfter(maxDate));
			Assume.that(!min.isAfter(max));
			Assume.that(!(minDate.isBefore(min.toLocalDate()) && maxDate.isBefore(min.toLocalDate())));
			Assume.that(!(minDate.isAfter(max.toLocalDate()) && maxDate.isAfter(max.toLocalDate())));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(min, max).dateBetween(minDate, maxDate);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime).isBetween(min, max);
				assertThat(dateTime.toLocalDate()).isBetween(minDate, maxDate);
				return true;
			});

		}

	}

	@Group
	class YearMethods {

		@Property
		void yearBetween(@ForAll("years") int min, @ForAll("years") int max, @ForAll JqwikRandom random) {

			Assume.that(min <= max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().yearBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(min);
				assertThat(dateTime.getYear()).isLessThanOrEqualTo(max);
				return true;
			});

		}

		@Property
		void yearBetweenSame(@ForAll("years") int year, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().yearBetween(year, year);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getYear()).isEqualTo(year);
				return true;
			});

		}

		@Property
		void yearBetweenMinAfterMax(@ForAll("years") int min, @ForAll("years") int max, @ForAll JqwikRandom random) {

			Assume.that(min > max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().yearBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(max);
				assertThat(dateTime.getYear()).isLessThanOrEqualTo(min);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> years() {
			return Arbitraries.integers().between(1, LocalDateTime.MAX.getYear());
		}

	}

	@Group
	class MonthMethods {

		@Property
		void monthBetween(@ForAll("months") int min, @ForAll("months") int max, @ForAll JqwikRandom random) {

			Assume.that(min <= max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().monthBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.of(min));
				assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.of(max));
				return true;
			});

		}

		@Property
		void monthBetweenSame(@ForAll("months") int month, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().monthBetween(month, month);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getMonth()).isEqualTo(Month.of(month));
				return true;
			});

		}

		@Property
		void monthBetweenMinAfterMax(@ForAll("months") int min, @ForAll("months") int max, @ForAll JqwikRandom random) {

			Assume.that(min > max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().monthBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.of(max));
				assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.of(min));
				return true;
			});

		}

		@Property
		void onlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().onlyMonths(months.toArray(new Month[]{}));

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getMonth()).isIn(months);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> months() {
			return Arbitraries.integers().between(1, 12);
		}

	}

	@Group
	class DayOfMonthMethods {

		@Property
		void dayOfMonthBetween(
			@ForAll("dayOfMonths") int min,
			@ForAll("dayOfMonths") int max,
			@ForAll JqwikRandom random
		) {

			Assume.that(min <= max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().dayOfMonthBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(min);
				assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(max);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenStartAfterEnd(
			@ForAll("dayOfMonths") int min,
			@ForAll("dayOfMonths") int max,
			@ForAll JqwikRandom random
		) {

			Assume.that(min > max);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().dayOfMonthBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(max);
				assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(min);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().dayOfMonthBetween(dayOfMonth, dayOfMonth);

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getDayOfMonth()).isEqualTo(dayOfMonth);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> dayOfMonths() {
			return Arbitraries.integers().between(1, 31);
		}

	}

	@Group
	class OnlyDaysOfWeekMethods {

		@Property
		void onlyDaysOfWeek(@ForAll @Size(min = 1) Set<DayOfWeek> dayOfWeeks, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

			checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime.getDayOfWeek()).isIn(dayOfWeeks);
				return true;
			});
		}

	}

}
