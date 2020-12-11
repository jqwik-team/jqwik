package net.jqwik.api.time.copy;

//COPY OF jqwik.engine.main

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.api.*;

public class SourceOfRandomness {

	private SourceOfRandomness() {
	}

	private static final Supplier<Random> RNG = ThreadLocalRandom::current;

	private static final ThreadLocal<Random> current = ThreadLocal.withInitial(SourceOfRandomness::newRandom);

	public static String createRandomSeed() {
		return Long.toString(RNG.get().nextLong());
	}

	public static Random create(String seed) {
		try {
			Random random = newRandom(Long.parseLong(seed));
			current.set(random);
			return random;
		} catch (NumberFormatException nfe) {
			throw new JqwikException(String.format("[%s] is not a valid random seed.", seed));
		}
	}

	public static Random newRandom() {
		return new XORShiftRandom();
	}

	public static Random newRandom(final long seed) {
		return new XORShiftRandom(seed);
	}

	public static Random current() {
		return current.get();
	}

	/**
	 * A faster but not thread safe implementation of {@linkplain Random}.
	 * It also has a period of 2^n - 1 and better statistical randomness.
	 *
	 * See for details: https://www.javamex.com/tutorials/random_numbers/xorshift.shtml
	 *
	 * <p>
	 * For further performance improvements within jqwik, consider to override:
	 * <ul>
	 *     <li>nextDouble()</li>
	 *     <li>nextBytes(int)</li>
	 * </ul>
	 */
	private static class XORShiftRandom extends Random {
		private long seed;

		private XORShiftRandom() {
			this(System.nanoTime());
		}

		private XORShiftRandom(long seed) {
			if (seed == 0l) {
				throw new IllegalArgumentException("0L is not an allowed seed value");
			}
			this.seed = seed;
		}

		@Override
		protected int next(int nbits) {
			long x = nextLong();
			x &= ((1L << nbits) - 1);
			return (int) x;
		}

		/**
		 * Will never generate 0L
		 */
		@Override
		public long nextLong() {
			long x = this.seed;
			x ^= (x << 21);
			x ^= (x >>> 35);
			x ^= (x << 4);
			this.seed = x;
			return x;
		}
	}
}
