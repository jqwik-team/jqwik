package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

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

		private DayOfWeek[] dayOfWeeks;

		@Property
		void only(@ForAll("onlyDayOfWeeks") DayOfWeek dayOfWeek) {
			assertThat(dayOfWeek).isIn(dayOfWeeks);
		}

		@Provide
		Arbitrary<DayOfWeek> onlyDayOfWeeks() {
			dayOfWeeks = generateDayOfWeeks();
			return Dates.daysOfWeek().only(dayOfWeeks);
		}

	}

	public static DayOfWeek[] generateDayOfWeeks(){
		int count = Arbitraries.integers().between(1, 7).sample();
		Arbitrary<DayOfWeek> dayOfWeekArbitrary = Arbitraries.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		ArrayList<DayOfWeek> dayOfWeekArrayList = new ArrayList<>();
		for(int i = 0; i < count; i++){
			DayOfWeek toAdd = dayOfWeekArbitrary.sample();
			dayOfWeekArbitrary = dayOfWeekArbitrary.filter(v -> !v.equals(toAdd));
			dayOfWeekArrayList.add(toAdd);
		}
		return dayOfWeekArrayList.toArray(new DayOfWeek[]{});
	}

}
