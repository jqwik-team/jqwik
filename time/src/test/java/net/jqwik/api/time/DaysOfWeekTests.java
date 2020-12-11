package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.time.copy.ArbitraryTestHelper.*;

@Group
public class DaysOfWeekTests {

	@Property
	void validDayOfWeekIsGenerated(@ForAll("dayOfWeeks") DayOfWeek dayOfWeek) {
		assertThat(dayOfWeek).isNotNull();
	}

	@Provide
	Arbitrary<DayOfWeek> dayOfWeeks() {
		return Dates.daysOfWeek();
	}

	@Group
	class CheckDaysOfWeekMethods {

		@Property
		void only(@ForAll("onlyDayOfWeeks") DayOfWeek[] dayOfWeeks) {

			Arbitrary<DayOfWeek> daysOfWeekArbitrary = Dates.daysOfWeek().only(dayOfWeeks);

			assertAllGenerated(daysOfWeekArbitrary.generator(1000), dayOfWeek -> {
				assertThat(dayOfWeek).isIn(dayOfWeeks);
			});
		}

		@Provide
		Arbitrary<DayOfWeek[]> onlyDayOfWeeks() {
			return generateDayOfWeeks();
		}

	}

	public static Arbitrary<DayOfWeek[]> generateDayOfWeeks(){
		Arbitrary<DayOfWeek> dayOfWeekArbitrary = Arbitraries.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Arbitrary<Integer> length = Arbitraries.integers().between(1, 7);
		Arbitrary<List<DayOfWeek>> arbitrary = length.flatMap(depth -> Arbitraries.recursive(
				() -> dayOfWeekArbitrary.map(v -> new ArrayList<>()),
				(v) -> addDayOfWeek(v, dayOfWeekArbitrary),
				depth
		));
		return arbitrary.map(v -> v.toArray(new DayOfWeek[]{}));
	}

	private static Arbitrary<List<DayOfWeek>> addDayOfWeek(Arbitrary<List<DayOfWeek>> listArbitrary, Arbitrary<DayOfWeek> monthArbitrary){
		return Combinators.combine(listArbitrary, monthArbitrary).as(DaysOfWeekTests::addToList);
	}

	private static List<DayOfWeek> addToList(List<DayOfWeek> list, DayOfWeek dayOfWeek){
		if(!list.contains(dayOfWeek)){
			list.add(dayOfWeek);
		}
		return list;
	}

}
