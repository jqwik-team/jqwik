package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.arbitraries.*;

public abstract class LocalDateTimeSupport<T> extends ArbitraryDecorator<T> {

	protected LocalDate minDate = null;
	protected LocalDate maxDate = null;
	protected Month minMonth = null;
	protected Month maxMonth = null;
	protected int minDayOfMonth = -1;
	protected int maxDayOfMonth = -1;

	protected ChronoUnit ofPrecision = DefaultLocalTimeArbitrary.DEFAULT_PRECISION;
	protected boolean ofPrecisionSet = false;

}
