package net.jqwik.discovery;

import net.jqwik.support.*;
import org.junit.platform.commons.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.UniqueId.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

import static java.util.stream.Collectors.*;

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
			LOG.warning(() -> String.format("Method id [%s] must follow '<method-name>([<list-of-parameter-types>])'", methodId,
				METHOD_PATTERN));
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

	/**
	 * UniqueId.toString/parse cannot handle representation of Array types in method string.
	 * TODO: Remove if bug is fixed in JUnit5 platform
	 */
	public static String toString(UniqueId uniqueId) {
		return uniqueId.getSegments().stream() //
			.map(segment -> describe(segment)) //
			.collect(joining(String.valueOf('/')));
	}

	private static String describe(Segment segment) {
		return String.format("[%s:%s]", segment.getType(), segment.getValue().replace('[', '{'));
	}

	/**
	 * UniqueId.toString/parse cannot handle representation of Array types in method string
	 * TODO: Remove if bug is fixed in JUnit5 platform
	 */
	public static UniqueId parse(String source) throws JUnitException {
		UniqueId parsedId = UniqueId.parse(source);
		List<Segment> segments = parsedId.getSegments();
		Segment root = segments.remove(0);
		UniqueId id = UniqueId.root(root.getType(), root.getValue());
		return appendToId(id, segments);
	}

	private static UniqueId appendToId(UniqueId id, List<Segment> segments) {
		if (segments.isEmpty())
			return id;
		Segment head = segments.remove(0);
		UniqueId appendedId = id.append(head.getType(), head.getValue().replace('{', '['));
		return appendToId(appendedId, segments);
	}

}
