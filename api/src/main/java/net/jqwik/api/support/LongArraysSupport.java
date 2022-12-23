package net.jqwik.api.support;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class LongArraysSupport {

	public static long at(long[] array, int i) {
		return array.length > i ? array[i] : 0;
	}

	public static long[] sumUpArrays(long[] left, long[] right) {
		long[] sum = new long[Math.max(left.length, right.length)];
		for (int i = 0; i < sum.length; i++) {
			long summedValue = at(left, i) + at(right, i);
			if (summedValue < 0) {
				summedValue = Long.MAX_VALUE;
			}
			sum[i] = summedValue;
		}
		return sum;
	}

	public static long[] concatArrays(long[] left, long[] right) {
		long[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}
}
