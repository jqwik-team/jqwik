package net.jqwik.api.support;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The methods in this class mimic the behaviour of {@link java.util.Objects#hash(Object...)} ()}
 * but do not create an array on the way.
 */
@API(status = INTERNAL)
public class HashCodeSupport {

	private HashCodeSupport() {
	}

	public static int hash(@Nullable Object o) {
		return baseHash(o) + 31;
	}

	private static int baseHash(@Nullable Object o) {
		if (o == null)
			return 0;
		return o.hashCode();
	}

	public static int hash(@Nullable Object o1, @Nullable Object o2) {
		return 31 * hash(o1) + baseHash(o2);
	}

	public static int hash(@Nullable Object o1, @Nullable Object o2, @Nullable Object o3) {
		return 31 * hash(o1, o2) + baseHash(o3);
	}

	public static int hash(@Nullable Object o1, @Nullable Object o2, @Nullable Object o3, @Nullable Object o4) {
		return 31 * hash(o1, o2, o3) + baseHash(o4);
	}

	public static int hash(@Nullable Object o1, @Nullable Object o2, @Nullable Object o3, @Nullable Object o4, @Nullable Object o5) {
		return 31 * hash(o1, o2, o3, o4) + baseHash(o5);
	}

	public static int hash(
		@Nullable Object o1,
		@Nullable Object o2,
		@Nullable Object o3,
		@Nullable Object o4,
		@Nullable Object o5,
		@Nullable Object o6
	) {
		return 31 * hash(o1, o2, o3, o4, o5) + baseHash(o6);
	}

	public static int hash(
		@Nullable Object o1,
		@Nullable Object o2,
		@Nullable Object o3,
		@Nullable Object o4,
		@Nullable Object o5,
		@Nullable Object o6,
		@Nullable Object o7
	) {
		return 31 * hash(o1, o2, o3, o4, o5, o6) + baseHash(o7);
	}

	public static int hash(
		@Nullable Object o1,
		@Nullable Object o2,
		@Nullable Object o3,
		@Nullable Object o4,
		@Nullable Object o5,
		@Nullable Object o6,
		@Nullable Object o7,
		@Nullable Object o8
	) {
		return 31 * hash(o1, o2, o3, o4, o5, o6, o7) + baseHash(o8);
	}

}
