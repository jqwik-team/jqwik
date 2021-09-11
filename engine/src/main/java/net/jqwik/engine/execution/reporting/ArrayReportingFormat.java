package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;

public class ArrayReportingFormat implements SampleReportingFormat {
	@Override
	public boolean appliesTo(Object value) {
		return value.getClass().isArray();
	}

	@Override
	public Object report(Object value) {
		Class<?> arrayClass = value.getClass();
		if (arrayClass.equals(byte[].class)) {
			List<Byte> list = new ArrayList<>();
			for (byte v : ((byte[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(short[].class)) {
			List<Short> list = new ArrayList<>();
			for (short v : ((short[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(int[].class)) {
			List<Integer> list = new ArrayList<>();
			for (int v : ((int[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(long[].class)) {
			List<Long> list = new ArrayList<>();
			for (long v : ((long[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(char[].class)) {
			List<Character> list = new ArrayList<>();
			for (char v : ((char[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(float[].class)) {
			List<Float> list = new ArrayList<>();
			for (float v : ((float[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(double[].class)) {
			List<Double> list = new ArrayList<>();
			for (double v : ((double[]) value)) {
				list.add(v);
			}
			return list;
		}
		if (arrayClass.equals(boolean[].class)) {
			List<Boolean> list = new ArrayList<>();
			for (boolean v : ((boolean[]) value)) {
				list.add(v);
			}
			return list;
		}
		return Arrays.asList((Object[]) value);
	}

	@Override
	public Optional<String> label(Object value) {
		Class<?> arrayClass = value.getClass();
		String baseClassName = arrayClass.getTypeName().startsWith("java.lang")
			? arrayClass.getSimpleName()
			: arrayClass.getTypeName();
		return Optional.of(String.format("%s ", baseClassName));
	}
}
