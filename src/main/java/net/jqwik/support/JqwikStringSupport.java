package net.jqwik.support;

import org.junit.platform.commons.util.*;

import java.util.*;
import java.util.stream.*;

public class JqwikStringSupport {
	public static String nullSafeToString(Class<?>... classes) {
		return StringUtils.nullSafeToString(classes);
	}

	public static String displayString(Object object) {
		if (object == null)
			return "null";
		if (object instanceof Collection) {
			Collection<Object> collection = (Collection) object;
			String elements = collection.stream().map(o -> displayString(o)).collect(Collectors.joining(", "));
			return String.format("[%s]", elements);
		}
		if (object.getClass().isArray()) {
			Object[] array = (Object[]) object;
			String elements = Arrays.stream(array).map(o -> displayString(o)).collect(Collectors.joining(", "));
			return String.format("%s{%s}", object.getClass().getSimpleName(), elements);
		}
		return object.toString();
	}

}
