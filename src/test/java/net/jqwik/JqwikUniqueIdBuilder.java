
package net.jqwik;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.discovery.*;

/**
 * For testing purposes
 */
public class JqwikUniqueIdBuilder {

	public static UniqueId uniqueIdForClassContainer(Class<?> ... containerClasses) {
		return uniqueIdForClasses(engineId(), new ArrayList<>(Arrays.asList(containerClasses)));
	}

	private static UniqueId uniqueIdForClasses(UniqueId parentId, List<Class<?>> containerClasses) {
		if (containerClasses.isEmpty())
			return parentId;
		Class<?> nextContainer = containerClasses.remove(0);
		UniqueId nextContainerId = JqwikUniqueIDs.appendContainer(parentId, nextContainer);
		return uniqueIdForClasses(nextContainerId, containerClasses);
	}

	public static UniqueId uniqueIdForExampleMethod(Class<?> containerClass, String methodName) {
		return uniqueIdForClassContainer(containerClass).append(JqwikUniqueIDs.EXAMPLE_SEGMENT_TYPE, methodName + "()");
	}

	public static UniqueId engineId() {
		return UniqueId.forEngine(JqwikTestEngine.ENGINE_ID);
	}

}
