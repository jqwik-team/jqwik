package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
public class MonthTests {

	@Property
	void validMonthIsGenerated(@ForAll("months") Month month) {
		assertThat(month).isNotNull();
	}

	@Provide
	Arbitrary<Month> months() {
		return Dates.months();
	}

	@Group
	class CheckMonthMethods {

		@Property
		void atTheEarliest(@ForAll("months") Month month, @ForAll Random random) {

			Arbitrary<Month> months = Dates.months().atTheEarliest(month);

			assertAllGenerated(months.generator(1000), random, m -> {
				assertThat(m).isGreaterThanOrEqualTo(month);
				return true;
			});

		}

		@Property
		void atTheLatest(@ForAll("months") Month month, @ForAll Random random) {

			Arbitrary<Month> months = Dates.months().atTheLatest(month);

			assertAllGenerated(months.generator(1000), random, m -> {
				assertThat(m).isLessThanOrEqualTo(month);
				return true;
			});
		}

		@Property
		void between(@ForAll("months") Month startMonth, @ForAll("months") Month endMonth, @ForAll Random random) {

			Assume.that(startMonth.compareTo(endMonth) <= 0);

			Arbitrary<Month> months = Dates.months().between(startMonth, endMonth);

			assertAllGenerated(months.generator(1000), random, month -> {
				assertThat(month).isGreaterThanOrEqualTo(startMonth);
				assertThat(month).isLessThanOrEqualTo(endMonth);
				return true;
			});

		}

		@Property
		void betweenSame(@ForAll("months") Month month, @ForAll Random random) {

			Arbitrary<Month> months = Dates.months().between(month, month);

			assertAllGenerated(months.generator(1000), random, m -> {
				assertThat(m).isEqualTo(month);
				return true;
			});
		}

		@Property
		void only(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

			Arbitrary<Month> monthArbitrary = Dates.months().only(months.toArray(new Month[]{}));

			assertAllGenerated(monthArbitrary.generator(1000), random, month -> {
				assertThat(month).isIn(months);
				return true;
			});
		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			MonthArbitrary months = Dates.months();
			Month value = shrinkToMinimal(months, random);
			assertThat(value).isEqualTo(Month.JANUARY);
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			MonthArbitrary months = Dates.months();
			TestingFalsifier<Month> falsifier = month -> month.compareTo(Month.MARCH) < 0;
			Month value = shrinkToMinimal(months, random, falsifier);
			assertThat(value).isEqualTo(Month.MARCH);
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Property(tries = 5)
		void containsAllValues() {
			Optional<ExhaustiveGenerator<Month>> optionalGenerator = Dates.months().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Month> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12); // Cannot know the number of filtered elements in advance
			assertThat(generator)
					.containsExactly(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER);
		}

		@Property(tries = 5)
		void between() {
			Optional<ExhaustiveGenerator<Month>> optionalGenerator = Dates.months().between(Month.MARCH, Month.JULY).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Month> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5); // Cannot know the number of filtered elements in advance
			assertThat(generator)
					.containsExactly(Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY);
		}

		@Property(tries = 5)
		void only() {
			Optional<ExhaustiveGenerator<Month>> optionalGenerator = Dates.months()
																		  .only(Month.JANUARY, Month.MARCH, Month.APRIL, Month.DECEMBER)
																		  .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Month> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12); // Cannot know the number of filtered elements in advance
			assertThat(generator)
					.containsExactly(Month.JANUARY, Month.MARCH, Month.APRIL, Month.DECEMBER);
		}

	}

	@Group
	class EdgeCasesTests {

		@Property(tries = 5)
		void all() {

			MonthArbitrary months = Dates.months();
			Set<Month> edgeCases = collectEdgeCases(months.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).contains(Month.JANUARY, Month.DECEMBER);

		}

		@Property(tries = 5)
		void between() {

			MonthArbitrary months = Dates.months().between(Month.MARCH, Month.AUGUST);
			Set<Month> edgeCases = collectEdgeCases(months.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).contains(Month.MARCH, Month.AUGUST);

		}

	}

}
