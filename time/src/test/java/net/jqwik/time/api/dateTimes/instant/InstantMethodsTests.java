package net.jqwik.time.api.dateTimes.instant;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class InstantMethodsTests {

	@Provide
	Arbitrary<Instant> instants() {
		return DateTimes.instants();
	}

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
	Arbitrary<Instant> precisionHours() {
		return DateTimes.instants().ofPrecision(HOURS);
	}

	@Property
	void atTheEarliest(@ForAll("instants") Instant min, @ForAll JqwikRandom random) {

		Arbitrary<Instant> instants = DateTimes.instants().atTheEarliest(min);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(instant).isAfterOrEqualTo(min);
			return true;
		});

	}

	@Property
	void atTheLatest(@ForAll("instants") Instant max, @ForAll JqwikRandom random) {

		Arbitrary<Instant> instants = DateTimes.instants().atTheLatest(max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(instant).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void between(@ForAll("instants") Instant min, @ForAll("instants") Instant max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<Instant> instants = DateTimes.instants().between(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(instant).isAfterOrEqualTo(min);
			assertThat(instant).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void betweenMinAfterMax(@ForAll("instants") Instant min, @ForAll("instants") Instant max, @ForAll JqwikRandom random) {

		Assume.that(min.isAfter(max));

		Arbitrary<Instant> instants = DateTimes.instants().between(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(instant).isAfterOrEqualTo(max);
			assertThat(instant).isBeforeOrEqualTo(min);
			return true;
		});

	}

	@Property
	void dateBetween(@ForAll LocalDate min, @ForAll LocalDate max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<Instant> instants = DateTimes.instants().dateBetween(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).toLocalDate()).isAfterOrEqualTo(min);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).toLocalDate()).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void yearBetween(@ForAll("years") int min, @ForAll("years") int max, @ForAll JqwikRandom random) {

		Assume.that(min <= max);

		Arbitrary<Instant> instants = DateTimes.instants().yearBetween(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getYear()).isGreaterThanOrEqualTo(min);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getYear()).isLessThanOrEqualTo(max);
			return true;
		});

	}

	@Property
	void monthBetween(@ForAll("months") int min, @ForAll("months") int max, @ForAll JqwikRandom random) {

		Assume.that(min <= max);

		Arbitrary<Instant> instants = DateTimes.instants().monthBetween(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMonth()).isGreaterThanOrEqualTo(Month.of(min));
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMonth()).isLessThanOrEqualTo(Month.of(max));
			return true;
		});

	}

	@Property
	void onlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

		Arbitrary<Instant> instants = DateTimes.instants().onlyMonths(months.toArray(new Month[]{}));

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMonth()).isIn(months);
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

		Arbitrary<Instant> instants = DateTimes.instants().dayOfMonthBetween(min, max);

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getDayOfMonth()).isGreaterThanOrEqualTo(min);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getDayOfMonth()).isLessThanOrEqualTo(max);
			return true;
		});

	}

	@Property
	void onlyDaysOfWeek(@ForAll @Size(min = 1) Set<DayOfWeek> dayOfWeeks, @ForAll JqwikRandom random) {

		Arbitrary<Instant> instants = DateTimes.instants().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

		checkAllGenerated(instants.generator(1000, true), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getDayOfWeek()).isIn(dayOfWeeks);
			return true;
		});
	}

	@Property
	void timeBetween(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<Instant> instants = DateTimes.instants().timeBetween(min, max);

		checkAllGenerated(instants.generator(1000), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).toLocalTime()).isAfterOrEqualTo(min);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).toLocalTime()).isBeforeOrEqualTo(max);
			return true;
		});
	}

	@Property
	void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll JqwikRandom random) {

		Assume.that(startHour <= endHour);

		Arbitrary<Instant> instants = DateTimes.instants().hourBetween(startHour, endHour);

		checkAllGenerated(instants.generator(1000), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getHour()).isGreaterThanOrEqualTo(startHour);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getHour()).isLessThanOrEqualTo(endHour);
			return true;
		});

	}

	@Property
	void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll JqwikRandom random) {

		Assume.that(startMinute <= endMinute);

		Arbitrary<Instant> instants = DateTimes.instants().minuteBetween(startMinute, endMinute);

		checkAllGenerated(instants.generator(1000), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMinute()).isGreaterThanOrEqualTo(startMinute);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMinute()).isLessThanOrEqualTo(endMinute);
			return true;
		});

	}

	@Property
	void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll JqwikRandom random) {

		Assume.that(startSecond <= endSecond);

		Arbitrary<Instant> instants = DateTimes.instants().secondBetween(startSecond, endSecond);

		checkAllGenerated(instants.generator(1000), random, instant -> {
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getSecond()).isGreaterThanOrEqualTo(startSecond);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getSecond()).isLessThanOrEqualTo(endSecond);
			return true;
		});

	}

	@Property
	void hours(@ForAll("precisionHours") Instant instant) {
		assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getMinute()).isEqualTo(0);
		assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getSecond()).isEqualTo(0);
		assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant).getNano()).isEqualTo(0);
	}

}
