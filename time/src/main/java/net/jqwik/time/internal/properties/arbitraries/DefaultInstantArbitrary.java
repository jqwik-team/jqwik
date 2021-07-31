package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultInstantArbitrary extends ArbitraryDecorator<Instant> implements InstantArbitrary {

	private LocalDateTimeArbitrary dateTimeArbitrary;

	public DefaultInstantArbitrary() {
		dateTimeArbitrary = DateTimes.dateTimes();
	}

	@Override
	protected Arbitrary<Instant> arbitrary() {

		return dateTimeArbitrary.map(dateTime -> dateTime.toInstant(ZoneOffset.UTC));

	}

}
