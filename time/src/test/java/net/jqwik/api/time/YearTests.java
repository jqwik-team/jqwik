package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
class YearTests {

	@Property
	void validYearIsGenerated(@ForAll("years") Year year) {
		assertThat(year).isNotNull();
	}

	@Provide
	Arbitrary<Year> years() {
		return Dates.years();
	}

	@Group
	class CheckYearMethods {

		@Property
		void greaterOrEqual(@ForAll("years") Year year, @ForAll Random random) {

			Arbitrary<Year> years = Dates.years().greaterOrEqual(year);

			assertAllGenerated(years.generator(1000), random, y -> {
				assertThat(y).isGreaterThanOrEqualTo(year);
				return true;
			});

		}

		@Property
		void lessOrEqual(@ForAll("years") Year year, @ForAll Random random) {

			Arbitrary<Year> years = Dates.years().lessOrEqual(year);

			assertAllGenerated(years.generator(1000), random, y -> {
				assertThat(y).isLessThanOrEqualTo(year);
				return true;
			});

		}

		@Property
		void between(@ForAll("years") Year startYear, @ForAll("years") Year endYear, @ForAll Random random) {

			Assume.that(startYear.compareTo(endYear) <= 0);

			Arbitrary<Year> years = Dates.years().between(startYear, endYear);

			assertAllGenerated(years.generator(1000), random, year -> {
				assertThat(year).isGreaterThanOrEqualTo(startYear);
				assertThat(year).isLessThanOrEqualTo(endYear);
				return true;
			});

		}

		@Property
		void betweenSame(@ForAll("years") Year year, @ForAll Random random) {

			Arbitrary<Year> years = Dates.years().between(year, year);

			assertAllGenerated(years.generator(1000), random, y -> {
				assertThat(y).isEqualTo(year);
				return true;
			});

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			YearArbitrary years = Dates.years();
			Year value = shrinkToMinimal(years, random);
			assertThat(value).isEqualTo(Year.of(0));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random){
			YearArbitrary years = Dates.years();
			TestingFalsifier<Year> falsifier = year -> year.getValue() < 42;
			Year value = shrinkToMinimal(years, random, falsifier);
			assertThat(value).isEqualTo(Year.of(42));
		}

		@Property
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random){
			YearArbitrary years = Dates.years();
			TestingFalsifier<Year> falsifier = year -> year.getValue() > -42;
			Year value = shrinkToMinimal(years, random, falsifier);
			assertThat(value).isEqualTo(Year.of(-42));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Property(tries = 5)
		void between() {
			Optional<ExhaustiveGenerator<Year>> optionalGenerator = Dates.years().between(-5, 5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Year> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(11);
			assertThat(generator)
					.containsExactly(Year.of(-5), Year.of(-4), Year.of(-3), Year.of(-2), Year.of(-1), Year.of(0), Year.of(1), Year.of(2), Year.of(3), Year.of(4), Year.of(5));
		}

	}

}
