package net.jqwik.engine;

import java.math.*;
import java.nio.*;

import net.jqwik.api.*;

import net.jqwik.api.random.*;

import net.jqwik.engine.random.*;

import org.apache.commons.rng.*;
import org.apache.commons.rng.core.*;
import org.apache.commons.rng.core.source64.*;
import org.apache.commons.rng.simple.*;
import org.jetbrains.annotations.*;
import sun.reflect.generics.reflectiveObjects.*;

public class SourceOfRandomness {

	private SourceOfRandomness() {
	}

	private static final RandomSource DEFAULT_ALGORITHM = RandomSource.L128_X1024_MIX;

	private static final ThreadLocal<JqwikRandom> current = ThreadLocal.withInitial(SourceOfRandomness::newRandom);

	private static String encodeBase36(byte[] bytes) {
		return new BigInteger(bytes).toString(36);
	}

	public static byte[] decodeBase36(String seed) {
		return new BigInteger(seed, 36).toByteArray();
	}
	
	public static String createRandomSeed() {
		return "1_" + DEFAULT_ALGORITHM.name() + "_" + encodeBase36(DEFAULT_ALGORITHM.createSeed());
	}

	public static JqwikRandomState createSeed(RandomSource algorithm, byte[] seed) {
		return new SeedBasedRandomState(algorithm, seed);
	}

	public static JqwikRandomState createSeed(RandomSource algorithm, long seed) {
		return createSeed(algorithm, longBytes(seed));
	}

	private static byte[] longBytes(long seed) {
		return ByteBuffer.allocate(Long.BYTES).putLong(seed).array();
	}

	public static JqwikRandomState createSeed(String seed) {
		RandomSource algorithm = DEFAULT_ALGORITHM;
		byte[] seedBytes;
		if (seed.startsWith("1_L128_X1024_MIX_")) {
			seedBytes = decodeBase36(seed.substring("1_L128_X1024_MIX_".length()));
		} else if (!seed.startsWith("1_")) {
			seedBytes = longBytes(Long.parseLong(seed));
		} else {
			throw new UnsupportedOperationException("TODO: implement parsing of 1_ seed");
		}
		return createSeed(algorithm, seedBytes);
	}

	public static JqwikRandom create(String seed) {
		try {
			JqwikRandomState state = createSeed(seed);
			JqwikRandom random = newRandom(state);
			current.set(random);
			return random;
		} catch (NumberFormatException nfe) {
			throw new JqwikException(String.format("[%s] is not a valid random seed.", seed));
		}
	}

	public static JqwikRandom newRandom() {
		return newRandom(RandomSource.createLong());
	}

	@Deprecated
	public static JqwikRandom newRandom(final long seed) {
		return newRandom(createSeed(DEFAULT_ALGORITHM, seed));
	}

	public static JqwikRandom newRandom(final JqwikRandomState seed) {
		return new JqwikRandomImpl(
			(RandomSource) seed.getAlgorithm(),
			((BaseRandomState) seed).createGenerator()
		);
	}

	public static JqwikRandom current() {
		return current.get();
	}
}
