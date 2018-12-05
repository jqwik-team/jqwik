package net.jqwik.discovery;

import net.jqwik.support.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.UniqueId.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

public class JqwikUniqueIDs {

	public static final String CONTAINER_SEGMENT_TYPE = "class";
	public static final String PROPERTY_SEGMENT_TYPE = "property";

	private static final Pattern METHOD_PATTERN = Pattern.compile("(.+)\\((.*)\\)");
	private static final Logger LOG = Logger.getLogger(JqwikUniqueIDs.class.getName());

	public static UniqueId appendProperty(UniqueId uniqueId, Method method) {
		return appendMethodSegment(uniqueId, method, PROPERTY_SEGMENT_TYPE);
	}

	public static UniqueId appendContainer(UniqueId uniqueId, Class<?> containerClass) {
		return uniqueId.append(CONTAINER_SEGMENT_TYPE, containerClass.getName());
	}

	public static Optional<Method> findMethodBySegment(Segment segment, Class<?> clazz) {
		String methodId = segment.getValue();
		Matcher matcher = METHOD_PATTERN.matcher(methodId);

		if (!matcher.matches()) {
			LOG.warning(() -> String.format("Method id [%s] must follow '<method-name>(<list-of-parameter-types>)'", methodId));
			return Optional.empty();
		}

		String methodName = matcher.group(1);
		String parameterTypeNames = matcher.group(2);
		return ReflectionSupport.findMethod(clazz, methodName, parameterTypeNames);
	}

	private static UniqueId appendMethodSegment(UniqueId uniqueId, Method method, String segmentType) {
		String methodId = String.format("%s(%s)", method.getName(), JqwikStringSupport.parameterTypesToString(method.getParameterTypes()));
		return uniqueId.append(segmentType, methodId);
	}

}
