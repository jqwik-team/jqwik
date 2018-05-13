package net.jqwik.support;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.commons.util.*;

public class JqwikStringSupport {
	public static String parameterTypesToString(Class<?>... classes) {
		return ClassUtils.nullSafeToString(classes);
	}

	public static String displayString(Object object) {
		if (object == null)
			return "null";
		if (object instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			String elements = collection.stream().map(JqwikStringSupport::displayString).collect(Collectors.joining(", "));
			return String.format("[%s]", elements);
		}
		if (object.getClass().isArray()) {
			if (object.getClass().getComponentType().isPrimitive()) {
				return StringUtils.nullSafeToString(object);
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

}
