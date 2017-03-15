package net.jqwik.discovery;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jqwik.support.JqwikStringSupport;
import org.junit.platform.engine.UniqueId;

import net.jqwik.support.JqwikReflectionSupport;

public class JqwikUniqueIDs {

	public static final String CONTAINER_SEGMENT_TYPE = "class";
	public static final String EXAMPLE_SEGMENT_TYPE = "example";
	public static final String PROPERTY_SEGMENT_TYPE = "property";

	private static final Pattern METHOD_PATTERN = Pattern.compile("(.+)\\((.*)\\)");

	private static final Logger LOG = Logger.getLogger(JqwikUniqueIDs.class.getName());

	public static UniqueId appendExample(UniqueId uniqueId, Method method) {
		return appendMethodSegment(uniqueId, method, EXAMPLE_SEGMENT_TYPE);
	}

	public static UniqueId appendProperty(UniqueId uniqueId, Method method) {
		return appendMethodSegment(uniqueId, method, PROPERTY_SEGMENT_TYPE);
	}

	public static UniqueId appendContainer(UniqueId uniqueId, Class<?> containerClass) {
		return uniqueId.append(CONTAINER_SEGMENT_TYPE, containerClass.getName());
	}

	public static Optional<Method> findMethodBySegment(UniqueId.Segment segment, Class<?> clazz) {
		String methodId = segment.getValue();
		Matcher matcher = METHOD_PATTERN.matcher(methodId);

		if (!matcher.matches()) {
			LOG.warning(() -> String.format("Method id [%s] must follow '<method-name>([<list-of-parameter-types>])'", methodId, METHOD_PATTERN));
			return Optional.empty();
		}

		String methodName = matcher.group(1);
		String parameterTypeNames = matcher.group(2);
		return JqwikReflectionSupport.findMethod(clazz, methodName, parameterTypeNames);
	}

	private static UniqueId appendMethodSegment(UniqueId uniqueId, Method method, String segmentType) {
		String methodId = String.format("%s(%s)", method.getName(), JqwikStringSupport.nullSafeToString(method.getParameterTypes()));
		return uniqueId.append(segmentType, methodId);
	}


}
