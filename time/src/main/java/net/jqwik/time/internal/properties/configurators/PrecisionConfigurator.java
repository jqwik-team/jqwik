package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PrecisionConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, Precision range) {
			ChronoUnit ofPrecision = range.ofPrecision();
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.ofPrecision(ofPrecision);
			} else {
				return arbitrary.filter(v -> filter(v, ofPrecision));
			}
		}

	}

	public static class ForLocalTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalTime.class);
		}

		public Arbitrary<LocalTime> configure(Arbitrary<LocalTime> arbitrary, Precision range) {
			ChronoUnit ofPrecision = range.ofPrecision();
			if (arbitrary instanceof LocalTimeArbitrary) {
				LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
				return localTimeArbitrary.ofPrecision(ofPrecision);
			} else {
				return arbitrary.filter(v -> filter(v, ofPrecision));
			}
		}

	}

	public static class ForOffsetTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(OffsetTime.class);
		}

		public Arbitrary<OffsetTime> configure(Arbitrary<OffsetTime> arbitrary, Precision range) {
			ChronoUnit ofPrecision = range.ofPrecision();
			if (arbitrary instanceof OffsetTimeArbitrary) {
				OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
				return offsetTimeArbitrary.ofPrecision(ofPrecision);
			} else {
				return arbitrary.filter(v -> filter(v, ofPrecision));
			}
		}

	}

	public static class ForDuration extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Duration.class);
		}

		public Arbitrary<Duration> configure(Arbitrary<Duration> arbitrary, Precision range) {
			ChronoUnit ofPrecision = range.ofPrecision();
			if (arbitrary instanceof DurationArbitrary) {
				DurationArbitrary durationArbitrary = (DurationArbitrary) arbitrary;
				return durationArbitrary.ofPrecision(ofPrecision);
			} else {
				return arbitrary.filter(v -> filter(v, ofPrecision));
			}
		}

	}

	private static boolean filter(int minute, int second, int nano, ChronoUnit ofPrecision) {
		switch (ofPrecision) {
			case HOURS:
				if (minute != 0) return false;
			case MINUTES:
				if (second != 0) return false;
			case SECONDS:
				if (nano != 0) return false;
				break;
			case MILLIS:
				if (nano % 1_000_000 != 0) return false;
				break;
			case MICROS:
				if (nano % 1_000 != 0) return false;
		}
		return true;
	}

	private static boolean filter(LocalDateTime dateTime, ChronoUnit ofPrecision) {
		return filter(dateTime.toLocalTime(), ofPrecision);
	}

	private static boolean filter(LocalTime time, ChronoUnit ofPrecision) {
		return filter(time.getMinute(), time.getSecond(), time.getNano(), ofPrecision);
	}

	private static boolean filter(OffsetTime offsetTime, ChronoUnit ofPrecision) {
		LocalTime time = offsetTime.toLocalTime();
		return filter(time, ofPrecision);
	}

	private static boolean filter(Duration duration, ChronoUnit ofPrecision) {
		int minutes = (int) ((duration.getSeconds() % 3600) / 60);
		int seconds = (int) (duration.getSeconds() % 60);
		int nanos = duration.getNano();
		return filter(minutes, seconds, nanos, ofPrecision);
	}

}
