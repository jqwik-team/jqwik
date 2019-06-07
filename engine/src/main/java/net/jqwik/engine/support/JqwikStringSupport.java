package net.jqwik.engine.support;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

public class JqwikStringSupport {
	public static String parameterTypesToString(Class<?>... classes) {
		return ClassSupport.nullSafeToString(classes);
	}

	public static String displayString(Object object) {
		if (object == null)
			return "null";
		if (object instanceof Class) {
			return ((Class) object).getName();
		}
		if (object instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			String elements = collection.stream().map(JqwikStringSupport::displayString).collect(Collectors.joining(", "));
			return String.format("[%s]", elements);
		}
		if (object.getClass().isArray()) {
			if (object.getClass().getComponentType().isPrimitive()) {
				return nullSafeToString(object);
			}
			Object[] array = (Object[]) object;
			String elements = Arrays.stream(array).map(JqwikStringSupport::displayString).collect(Collectors.joining(", "));
			return String.format("%s{%s}", object.getClass().getSimpleName(), elements);
		}
		if (String.class.isAssignableFrom(object.getClass())) {
			return String.format("\"%s\"", object.toString());
		}
		return object.toString();
	}

	private static String nullSafeToString(Object obj) {
		if (obj == null) {
			return "null";
		}

		try {
			if (obj.getClass().isArray()) {
				if (obj.getClass().getComponentType().isPrimitive()) {
					if (obj instanceof boolean[]) {
						return Arrays.toString((boolean[]) obj);
					}
					if (obj instanceof char[]) {
						return Arrays.toString((char[]) obj);
					}
					if (obj instanceof short[]) {
						return Arrays.toString((short[]) obj);
					}
					if (obj instanceof byte[]) {
						return Arrays.toString((byte[]) obj);
					}
					if (obj instanceof int[]) {
						return Arrays.toString((int[]) obj);
					}
					if (obj instanceof long[]) {
						return Arrays.toString((long[]) obj);
					}
					if (obj instanceof float[]) {
						return Arrays.toString((float[]) obj);
					}
					if (obj instanceof double[]) {
						return Arrays.toString((double[]) obj);
					}
				}
				return Arrays.deepToString((Object[]) obj);
			}

			// else
			return obj.toString();
		}
		catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);

			return defaultToString(obj);
		}
	}

	private static String defaultToString(Object obj) {
		if (obj == null) {
			return "null";
		}

		return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
	}


}
