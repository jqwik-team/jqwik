package net.jqwik.time.api.times.zoneOffset;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void zoneOffsetMin(@ForAll @OffsetRange(min = "-09:00:00") ZoneOffset offset) {
			assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0).getTotalSeconds());
		}

		@Property
		void zoneOffsetMax(@ForAll @OffsetRange(max = "+08:00:00") ZoneOffset offset) {
			assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(8, 0, 0).getTotalSeconds());
		}

		@Group
		class InvalidConfiguration {

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void minZoneOffsetTooEarly(@ForAll @OffsetRange(min = "-12:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void maxZoneOffsetTooEarly(@ForAll @OffsetRange(max = "-12:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void minZoneOffsetTooLate(@ForAll @OffsetRange(min = "+14:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void maxZoneOffsetTooLate(@ForAll @OffsetRange(max = "+14:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetUnsigned(@ForAll @OffsetRange(min = "07:00:00") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetUnsigned(@ForAll @OffsetRange(max = "07:00:00") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetIllegalString(@ForAll @OffsetRange(min = "foo") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetIllegalString(@ForAll @OffsetRange(max = "foo") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(min = "+2:3:2") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(max = "+2:3:2") ZoneOffset offset) {
				//do nothing
			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void offsetRange(@ForAll @OffsetRange(min = "-09:00:00", max = "+08:00:00") Long l) {
			assertThat(l).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void zoneOffsets(@ForAll("offsets") @OffsetRange(min = "+01:00:00", max = "-01:00:00") ZoneOffset offset) {
			assertThat(offset).isBetween(ZoneOffset.ofHoursMinutesSeconds(1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0));
		}

		@Provide
		Arbitrary<ZoneOffset> offsets() {
			return of(
				ZoneOffset.ofHours(-3),
				ZoneOffset.ofHours(-2),
				ZoneOffset.ofHours(-1),
				ZoneOffset.ofHours(0),
				ZoneOffset.ofHours(1),
				ZoneOffset.ofHours(2),
				ZoneOffset.ofHours(3)
			);
		}

	}

}
