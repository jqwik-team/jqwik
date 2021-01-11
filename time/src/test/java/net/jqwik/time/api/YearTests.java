package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class YearTests {

	@Provide
	Arbitrary<Year> years() {
		return Dates.years();
	}

	@Group
	class validYearsAreGenerated {

		@Property
		void yearIsNotNull(@ForAll("years") Year year) {
			assertThat(year).isNotNull();
		}

		@Property
		void defaultYearGenerationYearsOnlyBetween1900And2500(@ForAll("years") Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(1900);
			assertThat(year.getValue()).isLessThanOrEqualTo(2500);
		}

		@Property
		void yearIsNotZero(@ForAll("yearsAround0") Year year) {
			assertThat(year).isNotEqualTo(Year.of(0));
		}

		@Provide
		Arbitrary<Year> yearsAround0() {
			return Dates.years().between(-10, 10);
		}

	}

	@Group
	class ValidYearsAreGeneratedWithAnnotation {

		@Property
		void yearIsNotNull(@ForAll Year year) {
			assertThat(year).isNotNull();
		}

		@Property
		void defaultYearGenerationYearsOnlyBetween1900And2500(@ForAll Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(1900);
			assertThat(year.getValue()).isLessThanOrEqualTo(2500);
		}

		@Property
		void yearIsNotZero(@ForAll Year year) {
			assertThat(year).isNotEqualTo(Year.of(0));
		}

	}

	@Group
	class CheckYearMethods {

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
			Year value = falsifyThenShrink(years, random);
			assertThat(value).isEqualTo(Year.of(1900));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			YearArbitrary years = Dates.years();
			TestingFalsifier<Year> falsifier = year -> year.getValue() < 1942;
			Year value = falsifyThenShrink(years, random, falsifier);
			assertThat(value).isEqualTo(Year.of(1942));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<Year>> optionalGenerator = Dates.years().between(-5, 5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Year> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(11); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Year.of(-5),
					Year.of(-4),
					Year.of(-3),
					Year.of(-2),
					Year.of(-1),
					Year.of(1),
					Year.of(2),
					Year.of(3),
					Year.of(4),
					Year.of(5)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			YearArbitrary years = Dates.years();
			Set<Year> edgeCases = collectEdgeCases(years.edgeCases());
			assertThat(edgeCases).hasSize(4);
			assertThat(edgeCases)
					.containsExactlyInAnyOrder(Year.of(1900), Year.of(1901), Year.of(2499), Year.of(2500));
		}

		@Example
		void between() {
			YearArbitrary years = Dates.years().between(100, 200);
			Set<Year> edgeCases = collectEdgeCases(years.edgeCases());
			assertThat(edgeCases).hasSize(4);
			assertThat(edgeCases).containsExactlyInAnyOrder(Year.of(100), Year.of(101), Year.of(199), Year.of(200));
		}

	}

	@Group
	class CheckConstraints {

		@Property
		void yearBetweenMinus100And100(@ForAll @YearRange(min = -100, max = 100) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(-100);
			assertThat(year.getValue()).isLessThanOrEqualTo(100);
			assertThat(year).isNotEqualTo(Year.of(0));
		}

		@Property
		void yearBetween3000And3500(@ForAll @YearRange(min = 3000, max = 3500) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(3000);
			assertThat(year.getValue()).isLessThanOrEqualTo(3500);
		}

	}

}
