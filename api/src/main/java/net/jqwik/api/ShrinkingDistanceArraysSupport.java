package net.jqwik.api;

import java.util.*;

class ShrinkingDistanceArraysSupport {

	static long at(long[] array, int i) {
		return array.length > i ? array[i] : 0;
	}

	static long[] sumUp(List<long[]> listOfArrays) {
		long[] summedUpArray = new long[maxLength(listOfArrays)];
		for (long[] array : listOfArrays) {
			for (int i = 0; i < summedUpArray.length; i++) {
				summedUpArray[i] = plusWithoutOverflowAt(summedUpArray, array, i);
			}
		}
		return summedUpArray;
	}

	private static int maxLength(List<long[]> listOfArrays) {
		int maxDistanceSize = 0;
		for (long[] array : listOfArrays) {
			maxDistanceSize = Math.max(maxDistanceSize, array.length);
		}
		return maxDistanceSize;
	}

	private static long plusWithoutOverflowAt(long[] left, long[] right, int index) {
		long summedValue = at(right, index) + at(left, index);
		if (summedValue < 0) {
			return Long.MAX_VALUE;
		}
		return summedValue;
	}

	static long[] concatenate(List<long[]> listOfArrays) {
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
