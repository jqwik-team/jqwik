
package net.jqwik;

import org.junit.platform.engine.UniqueId;

import net.jqwik.discovery.JqwikUniqueIDs;

/**
 * For testing purposes
 */
public class JqwikUniqueIdBuilder {

	public static UniqueId uniqueIdForClassContainer(Class<?> containerClass) {
		return JqwikUniqueIDs.appendContainer(engineId(), containerClass);
	}

	public static UniqueId uniqueIdForExampleMethod(Class<?> containerClass, String methodName) {
		return uniqueIdForClassContainer(containerClass).append(JqwikUniqueIDs.EXAMPLE_SEGMENT_TYPE, methodName + "()");
	}

	public static UniqueId engineId() {
		return UniqueId.forEngine(JqwikTestEngine.ENGINE_ID);
	}

}
