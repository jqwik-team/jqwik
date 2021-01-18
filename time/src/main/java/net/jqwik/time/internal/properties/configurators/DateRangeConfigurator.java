package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DateRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class) || targetType.isAssignableFrom(Calendar.class) || targetType
																													  .isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DateRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.between(isoDateToLocalDate(range.min()), isoDateToLocalDate(range.max()));
		} else if (checkIfCalendar(arbitrary)) {
			return arbitrary; //TODO
		} else if (checkIfDate(arbitrary)) {
			return arbitrary; //TODO
		} else {
			return arbitrary;
		}
	}

	private LocalDate isoDateToLocalDate(String iso) {
		if (iso == null || iso.length() == 0) {
			return null;
		}
		String[] parts = iso.split("-");
		if (parts.length != 3) {
			return null;
		}
		int year, month, day;
		try {
			year = Integer.parseInt(parts[0]);
			month = Integer.parseInt(parts[1]);
			day = Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			return null;
		}
		return LocalDate.of(year, month, day);
	}

	private boolean checkIfCalendar(Arbitrary<?> arbitrary) {
		if (!arbitrary.allValues().isPresent()) {
			return false;
		}
		return arbitrary.allValues().get().allMatch(v -> v instanceof Calendar);
	}

	private boolean checkIfDate(Arbitrary<?> arbitrary) {
		if (!arbitrary.allValues().isPresent()) {
			return false;
		}
		return arbitrary.allValues().get().allMatch(v -> v instanceof Date);
	}

}
