package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	private ZoneOffset offsetMin = ZoneOffset.MIN;
	private ZoneOffset offsetMax = ZoneOffset.MAX;

	private int hourMin = -18;
	private int hourMax = 18;

	private int minuteMin = 0;
	private int minuteMax = 59;

	private int secondMin = 0;
	private int secondMax = 59;

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		if (hourMin == hourMax && hourMin == -18) {
			if (minuteMin != 0 || secondMin != 0) {
				throw new IllegalArgumentException("Minutes and Seconds must be 0 because hour value is -18.");
			} else if (offsetMin.getTotalSeconds() != ZoneOffset.MIN.getTotalSeconds()) {
				throw new IllegalArgumentException("Hour value can only be -18, but minimum zone offset value is not ZoneOffset.MIN");
			}
			return Arbitraries.just(ZoneOffset.MIN);
		} else if (hourMin == hourMax && hourMax == 18) {
			if (minuteMin != 0 || secondMin != 0) {
				throw new IllegalArgumentException("Minutes and Seconds must be 0 because hour value is 18.");
			} else if (offsetMax.getTotalSeconds() != ZoneOffset.MAX.getTotalSeconds()) {
				throw new IllegalArgumentException("Hour value can only be 18, but maximum zone offset value is not ZoneOffset.MAX");
			}
			return Arbitraries.just(ZoneOffset.MAX);
		}

		ZoneOffset effectiveMin = calculateEffectiveMin();
		ZoneOffset effectiveMax = calculateEffectiveMax();

		if (effectiveMin.getTotalSeconds() > effectiveMax.getTotalSeconds()) {
			throw new IllegalArgumentException("No values are possible with these configurations.");
		}

		Arbitrary<Integer> seconds = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(effectiveMin.getTotalSeconds(), effectiveMax.getTotalSeconds())
												.edgeCases(edgeCases -> edgeCases
																				.includeOnly(effectiveMin.getTotalSeconds(), 0, effectiveMax
																																		.getTotalSeconds()));

		Arbitrary<ZoneOffset> zoneOffsets = seconds.map(ZoneOffset::ofTotalSeconds)
												   .filter(this::filterOffsets);

		return zoneOffsets;

	}

	private ZoneOffset calculateEffectiveMin() {

		int hours = calculateHourValue(offsetMin);
		int minutes = calculateMinuteValue(offsetMin);
		int seconds = calculateSecondValue(offsetMin);

		if (hours < hourMin) {
			hours = hourMin;
			if (hours > 0) {
				if (minutes < minuteMin) {
					minutes = minuteMin;
					if (seconds < secondMin) {
						seconds = secondMin;
					}
				}
			} else if (hours < 0) {
				minutes = minuteMax;
				seconds = secondMax;
			} else {
				//hour is 0, set the other values for the next if(hours == 0)
				minutes = 59;
				seconds = 59;
			}
		}

		if (hours == 0) {

			if (offsetMin.getTotalSeconds() == 0) {
				//1)
				minutes = minuteMin;
				seconds = secondMin;
			} else if (offsetMin.getTotalSeconds() < 0) {
				//2
				if (minutes > minuteMax) {
					//2.1)
					minutes = -minuteMax;
					seconds = -secondMax;
				} else if (minutes >= minuteMin) {
					//2.2
					minutes = -minutes;
					if (seconds > secondMax) {
						//2.2.1)
						seconds = -secondMax;
					} else if (seconds >= secondMin) {
						//2.2.2)
						seconds = -seconds;
					} else {
						//2.2.3
						if (-minutes > minuteMin) {
							//2.2.3.1)
							minutes++;
							seconds = -secondMax;
						} else {
							//2.2.3.2)
							minutes = minuteMin;
							seconds = secondMin;
						}
					}
				} else {
					//2.3)
					minutes = minuteMin;
					seconds = secondMin;
				}
			} else {
				//3
				if (minutes > minuteMax) {
					//3.1)
					hours = 1;
					minutes = minuteMin;
					seconds = secondMin;
				} else if (minutes >= minuteMin) {
					//3.2
					if (seconds < secondMin) {
						//3.2.1)
						seconds = secondMin;
					} else if (seconds <= secondMax) {
						//3.2.2)
						//do nothing
					} else {
						//3.2.3
						seconds = secondMin;
						if (minutes == minuteMax) {
							//3.2.3.1)
							hours = 1;
							minutes = minuteMin;
						} else {
							//3.2.3.2)
							minutes++;
						}
					}
				} else {
					//3.3)
					minutes = minuteMin;
					seconds = secondMin;
				}
			}

		} else if (hours < 0) {
			minutes = -minutes;
			seconds = -seconds;
		}

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private ZoneOffset calculateEffectiveMax() {

		int hours = calculateHourValue(offsetMax);
		int minutes = calculateMinuteValue(offsetMax);
		int seconds = calculateSecondValue(offsetMax);

		if (hours > hourMax) {
			hours = hourMax;
			if (hours > 0) {
				minutes = minuteMax;
				seconds = secondMax;
			} else if (hours < 0) {
				if (minutes < minuteMin) {
					minutes = minuteMin;
					if (seconds < secondMin) {
						seconds = secondMin;
					}
				}
			} else {
				//hour is 0, set the other values for the next if(hours == 0)
				minutes = 59;
				seconds = 59;
			}
		}

		if (hours == 0) {

			if (offsetMax.getTotalSeconds() == 0) {
				//1)
				minutes = -minuteMin;
				seconds = -secondMin;
			} else if (offsetMax.getTotalSeconds() > 0) {
				//2
				if (minutes > minuteMax) {
					//2.1)
					minutes = minuteMax;
					seconds = secondMax;
				} else if (minutes >= minuteMin) {
					//2.2
					if (seconds > secondMax) {
						//2.2.1)
						seconds = secondMax;
					} else if (seconds >= secondMin) {
						//2.2.2)
						//do nothing
					} else {
						//2.2.3
						if (minutes == minuteMin) {
							//2.2.3.1)
							minutes = -minuteMin;
							seconds = -secondMin;
						} else {
							//2.2.3.2)
							minutes--;
							seconds = secondMax;
						}
					}
				} else {
					//2.3)
					minutes = -minuteMin;
					seconds = -secondMin;
				}
			} else {
				//3
				if (minutes > minuteMax) {
					//3.1)
					hours = -1;
					minutes = -minuteMin;
					seconds = -secondMin;
				} else if (minutes >= minuteMin) {
					//3.2
					minutes = -minutes;
					if (seconds < secondMin) {
						//3.2.1
						seconds = -secondMin;
					} else if (seconds <= secondMax) {
						//3.2.2)
						seconds = -seconds;
					} else {
						//3.2.3
						seconds = -secondMin;
						if (-minutes == minuteMax) {
							//3.2.3.1)
							hours = -1;
							minutes = -minuteMin;
						} else {
							//3.2.3.2
							minutes--;
						}
					}
				} else {
					//3.3)
					minutes = -minuteMin;
					seconds = -secondMin;
				}
			}

		} else if (hours < 0) {
			minutes = -minutes;
			seconds = -seconds;
		}

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private boolean filterOffsets(ZoneOffset offset) {

		int hour = calculateHourValue(offset);
		int minute = calculateMinuteValue(offset);
		int second = calculateSecondValue(offset);

		return hour >= hourMin && hour <= hourMax && minute >= minuteMin && minute <= minuteMax && second >= secondMin && second <= secondMax;

	}

	private int calculateHourValue(ZoneOffset offset) {
		return offset.getTotalSeconds() / 3600;
	}

	private int calculateMinuteValue(ZoneOffset offset) {
		return Math.abs((offset.getTotalSeconds() % 3600) / 60);
	}

	private int calculateSecondValue(ZoneOffset offset) {
		return Math.abs(offset.getTotalSeconds() % 60);
	}

	@Override
	public ZoneOffsetArbitrary atTheEarliest(ZoneOffset min) {
		if ((offsetMax != null) && min.getTotalSeconds() > offsetMax.getTotalSeconds()) {
			throw new IllegalArgumentException("Minimum offset must not be after maximum offset");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMin = min;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary atTheLatest(ZoneOffset max) {
		if ((offsetMin != null) && max.getTotalSeconds() < offsetMin.getTotalSeconds()) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary hourBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < -18 || max > 18) {
			throw new IllegalArgumentException("Hour value must be between -18 and 18.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.hourMin = min;
		clone.hourMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary minuteBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Minute value must be between 0 and 59.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.minuteMin = min;
		clone.minuteMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary secondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.secondMin = min;
		clone.secondMax = max;
		return clone;
	}

}
