package net.jqwik.time.api.testingSupport;

import java.util.*;

import net.jqwik.time.internal.properties.arbitraries.*;

import static net.jqwik.time.api.testingSupport.ForCalendar.*;

public class ForDate {

	public static Calendar dateToCalendar(Date date) {
		return DefaultDateArbitrary.dateToCalendar(date);
	}

	public static Date getDate(int year, int month, int day) {
		return getCalendar(year, month, day).getTime();
	}

}
