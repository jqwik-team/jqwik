
package net.jqwik.engine;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.discovery.*;

/**
 * For testing purposes
 */
public class JqwikUniqueIdBuilder {

	public static UniqueId uniqueIdForClassContainer(Class<?>... containerClasses) {
		List<Class<?>> classes = Arrays.asList(containerClasses);
		return uniqueIdForClasses(engineId(), new ArrayList<>(classes));
	}

	private static UniqueId uniqueIdForClasses(UniqueId parentId, List<Class<?>> containerClasses) {
		if (containerClasses.isEmpty())
			return parentId;
		Class<?> nextContainer = containerClasses.remove(0);
		UniqueId nextContainerId = JqwikUniqueIDs.appendContainer(parentId, nextContainer);
		return uniqueIdForClasses(nextContainerId, containerClasses);
	}

	public static UniqueId uniqueIdForPropertyMethod(Class<?> containerClass, String methodName) {
		return uniqueIdForClassContainer(containerClass).append(JqwikUniqueIDs.PROPERTY_SEGMENT_TYPE, methodName + "()");
	}

	public static UniqueId engineId() {
		return UniqueId.forEngine(JqwikTestEngine.ENGINE_ID);
	}

}
