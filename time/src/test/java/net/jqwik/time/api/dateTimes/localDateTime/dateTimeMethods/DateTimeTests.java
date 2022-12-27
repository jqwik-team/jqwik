package net.jqwik.time.api.dateTimes.localDateTime.dateTimeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class DateTimeTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Property
	void atTheEarliest(@ForAll("dateTimes") LocalDateTime min, @ForAll JqwikRandom random) {

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheEarliest(min);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isAfterOrEqualTo(min);
			return true;
		});

	}

	@Property
	void atTheEarliestAtTheLatestMinAfterMax(
		@ForAll("dateTimes") LocalDateTime min,
		@ForAll("dateTimes") LocalDateTime max,
		@ForAll JqwikRandom random
	) {

		Assume.that(min.isAfter(max));

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheEarliest(min).atTheLatest(max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isAfterOrEqualTo(max);
			assertThat(dateTime).isBeforeOrEqualTo(min);
			return true;
		});

	}

	@Property
	void atTheLatest(@ForAll("dateTimes") LocalDateTime max, @ForAll JqwikRandom random) {

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheLatest(max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void atTheLatestAtTheEarliestMinAfterMax(
		@ForAll("dateTimes") LocalDateTime min,
		@ForAll("dateTimes") LocalDateTime max,
		@ForAll JqwikRandom random
	) {

		Assume.that(min.isAfter(max));

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheLatest(max).atTheEarliest(min);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isAfterOrEqualTo(max);
			assertThat(dateTime).isBeforeOrEqualTo(min);
			return true;
		});

	}

	@Property
	void between(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max, @ForAll JqwikRandom random) {

		Assume.that(!min.isAfter(max));

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isAfterOrEqualTo(min);
			assertThat(dateTime).isBeforeOrEqualTo(max);
			return true;
		});

	}

	@Property
	void betweenMinAfterMax(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max, @ForAll JqwikRandom random) {

		Assume.that(min.isAfter(max));

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isAfterOrEqualTo(max);
			assertThat(dateTime).isBeforeOrEqualTo(min);
			return true;
		});

	}

	@Property
	void betweenSame(@ForAll("dateTimes") LocalDateTime same, @ForAll JqwikRandom random) {

		Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(same, same);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isEqualTo(same);
			return true;
		});

	}

}
