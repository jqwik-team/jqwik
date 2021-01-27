package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalTimeArbitrary extends ArbitraryDecorator<LocalTime> implements LocalTimeArbitrary {

	private LocalTime timeMin = LocalTime.MIN;
	private LocalTime timeMax = LocalTime.MAX;

	private int hourMin = 0;
	private int hourMax = 23;
	private int hourPeriod;

	private int minuteMin = 0;
	private int minuteMax = 59;
	private int minutePeriod;

	private int secondMin = 0;
	private int secondMax = 59;
	private int secondPeriod;

	private int millisecondMin = 0;
	private int millisecondMax = 999;
	private int millisecondPeriod;

	private int microsecondMin = 0;
	private int microsecondMax = 999;
	private int microsecondPeriod;

	private int nanosecondMin = 0;
	private int nanosecondMax = 999;
	private int nanosecondPeriod;

	private long helperMinuteSecondMilliMicroNano;
	private long helperSecondMilliMicroNano;
	private long helperMilliMicroNano;
	private long helperMicroNano;

	@Override
	protected Arbitrary<LocalTime> arbitrary() {

		LocalTime effectiveMin = calculateEffectiveMin();
		LocalTime effectiveMax = calculateEffectiveMax();

		Arbitrary<LocalTime> localTimes;

		if (timeMin != LocalTime.MIN && timeMax != LocalTime.MAX && valuesAreDefault()) {

			long longEnd = NANOS.between(effectiveMin, effectiveMax);

			Arbitrary<Long> longs = Arbitraries.longs()
											   .withDistribution(RandomDistribution.uniform())
											   .between(0L, longEnd);

			localTimes = longs.map(effectiveMin::plusNanos);

		} else {

			calculateEffectiveValues(effectiveMin, effectiveMax);

			long longEnd = calculateLongEnd();

			Arbitrary<Long> longs = Arbitraries.longs()
											   .withDistribution(RandomDistribution.uniform())
											   .between(0L, longEnd);

			localTimes = longs.map(this::calculateLocalTime);

			if (!effectiveMin.equals(LocalTime.MIN)) {
				localTimes = localTimes.filter(v -> !v.isBefore(effectiveMin));
			}

			if (!effectiveMax.equals(LocalTime.MAX)) {
				localTimes = localTimes.filter(v -> !v.isAfter(effectiveMax));
			}

		}

		localTimes = localTimes.edgeCases(edgeCases -> {
			edgeCases.includeOnly(effectiveMin, effectiveMax);
		});

		return localTimes;

	}

	private boolean valuesAreDefault() {
		if (hourMin != 0) return false;
		if (hourMax != 23) return false;
		if (minuteMin != 0) return false;
		if (minuteMax != 59) return false;
		if (secondMin != 0) return false;
		if (secondMax != 59) return false;
		if (millisecondMin != 0) return false;
		if (millisecondMax != 999) return false;
		if (microsecondMin != 0) return false;
		if (microsecondMax != 999) return false;
		if (nanosecondMin != 0) return false;
		if (nanosecondMax != 999) return false;
		return true;
	}

	private int calculateNanoValue(int milliseconds, int microseconds, int nanoseconds) {
		return milliseconds * 1_000_000 + microseconds * 1_000 + nanoseconds;
	}

	private LocalTime calculateEffectiveMin() {
		LocalTime effective = timeMin;
		if (hourMin > effective.getHour()) {
			effective = effective.withHour(hourMin);
			if (minuteMin > effective.getMinute()) {
				effective = effective.withMinute(minuteMin);
				if (secondMin > effective.getSecond()) {
					effective = effective.withSecond(secondMin);
					int nanoValue = calculateNanoValue(millisecondMin, microsecondMin, nanosecondMin);
					if (nanoValue > effective.getNano()) {
						effective = effective.withNano(nanoValue);
					}
				}
			}
		}
		return effective;
	}

	private LocalTime calculateEffectiveMax() {
		LocalTime effective = timeMax;
		if (hourMax < effective.getHour()) {
			effective = effective.withHour(hourMax);
			if (minuteMax < effective.getMinute()) {
				effective = effective.withMinute(minuteMax);
				if (secondMax < effective.getSecond()) {
					effective = effective.withSecond(secondMax);
					int nanoValue = calculateNanoValue(millisecondMax, microsecondMax, nanosecondMax);
					if (nanoValue < effective.getNano()) {
						effective = effective.withNano(nanoValue);
					}
				}
			}
		}
		return effective;
	}

	private void calculateEffectiveValues(LocalTime effectiveMin, LocalTime effectiveMax) {
		hourMin = effectiveMin.getHour();
		hourMax = effectiveMax.getHour();
		if (hourMin == hourMax) {
			minuteMin = effectiveMin.getMinute();
			minuteMax = effectiveMax.getMinute();
			if (minuteMin == minuteMax) {
				secondMin = effectiveMin.getSecond();
				secondMax = effectiveMax.getSecond();
				if (secondMin == secondMax) {
					millisecondMin = effectiveMin.getNano() / 1_000_000;
					millisecondMax = effectiveMax.getNano() / 1_000_000;
					if (millisecondMin == millisecondMax) {
						microsecondMin = (effectiveMin.getNano() % 1_000_000) / 1_000;
						microsecondMax = (effectiveMax.getNano() % 1_000_000) / 1_000;
						if (microsecondMin == microsecondMax) {
							nanosecondMin = effectiveMin.getNano() % 1_000;
							nanosecondMax = effectiveMax.getNano() % 1_000;
						}
					}
				}
			}
		}
	}

	private LocalTime calculateLocalTime(long l) {

		int hours = hourMin;
		int minutes = minuteMin;
		int seconds = secondMin;
		int milliseconds = millisecondMin;
		int microseconds = microsecondMin;
		int nanoseconds = nanosecondMin;

		long hourAdd = l / helperMinuteSecondMilliMicroNano;
		l -= helperMinuteSecondMilliMicroNano * hourAdd;
		hours += hourAdd;

		long minuteAdd = l / helperSecondMilliMicroNano;
		l -= helperSecondMilliMicroNano * minuteAdd;
		minutes += minuteAdd;

		long secondAdd = l / helperMilliMicroNano;
		l -= helperMilliMicroNano * secondAdd;
		seconds += secondAdd;

		long millisecondAdd = l / helperMicroNano;
		l -= helperMicroNano * millisecondAdd;
		milliseconds += millisecondAdd;

		long microsecondAdd = l / nanosecondPeriod;
		l -= nanosecondPeriod * microsecondAdd;
		microseconds += microsecondAdd;

		nanoseconds += l;

		int nanoValue = calculateNanoValue(milliseconds, microseconds, nanoseconds);

		return LocalTime.of(hours, minutes, seconds, nanoValue);
	}

	private long calculateLongEnd() {

		long l = 1;

		hourPeriod = 1 + hourMax - hourMin;
		l *= hourPeriod;

		minutePeriod = 1 + minuteMax - minuteMin;
		l *= minutePeriod;

		secondPeriod = 1 + secondMax - secondMin;
		l *= secondPeriod;

		millisecondPeriod = 1 + millisecondMax - millisecondMin;
		l *= millisecondPeriod;

		microsecondPeriod = 1 + microsecondMax - microsecondMin;
		l *= microsecondPeriod;

		nanosecondPeriod = 1 + nanosecondMax - nanosecondMin;
		l *= nanosecondPeriod;

		l -= 1;

		helperMicroNano = microsecondPeriod * nanosecondPeriod;
		helperMilliMicroNano = millisecondPeriod * helperMicroNano;
		helperSecondMilliMicroNano = secondPeriod * helperMilliMicroNano;
		helperMinuteSecondMilliMicroNano = minutePeriod * helperSecondMilliMicroNano;

		return l;

	}

	@Override
	public LocalTimeArbitrary atTheEarliest(LocalTime min) {
		if ((timeMax != null) && min.isAfter(timeMax)) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.timeMin = min;
		return clone;
	}

	@Override
	public LocalTimeArbitrary atTheLatest(LocalTime max) {
		if ((timeMin != null) && max.isBefore(timeMin)) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.timeMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary hourBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 23) {
			throw new IllegalArgumentException("Hour value must be between 0 and 23.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.hourMin = Math.max(min, 0);
		clone.hourMax = Math.min(max, 23);
		return clone;
	}

	@Override
	public LocalTimeArbitrary minuteBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Minute value must be between 0 and 59.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.minuteMin = min;
		clone.minuteMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary secondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.secondMin = min;
		clone.secondMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary millisecondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 999) {
			throw new IllegalArgumentException("Millisecond value must be between 0 and 999.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.millisecondMin = min;
		clone.millisecondMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary microsecondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 999) {
			throw new IllegalArgumentException("Microsecond value must be between 0 and 999.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.microsecondMin = min;
		clone.microsecondMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary nanosecondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 999) {
			throw new IllegalArgumentException("Nanosecond value must be between 0 and 999.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.nanosecondMin = min;
		clone.nanosecondMax = max;
		return clone;
	}
}
