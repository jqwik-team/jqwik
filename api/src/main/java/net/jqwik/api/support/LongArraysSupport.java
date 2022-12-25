package net.jqwik.api.support;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class LongArraysSupport {

	public static long at(long[] array, int i) {
		return array.length > i ? array[i] : 0;
	}

	public static long[] sumUp(long[] left, long[] right) {
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

	public static long[] concatenate(List<long[]> listOfArrays) {
		int size = listOfArrays.stream().mapToInt(s -> s.length).sum();
		long[] concatenatedArrays = new long[size];
		int i = 0;
		for (long[] array : listOfArrays) {
			System.arraycopy(array, 0, concatenatedArrays, i, array.length);
			i += array.length;
		}
		return concatenatedArrays;
	}
}
