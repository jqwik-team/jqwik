
package net.jqwik;

import net.jqwik.discovery.JqwikDiscoverer;
import org.junit.platform.engine.UniqueId;

public class JqwikUniqueIdBuilder {

	public static UniqueId uniqueIdForClassContainer(Class<?> clazz) {
		return engineId().append(JqwikDiscoverer.CONTAINER_SEGMENT_TYPE, clazz.getName());
	}

	public static UniqueId uniqueIdForExampleMethod(Class<?> clazz, String methodName) {
		return uniqueIdForClassContainer(clazz).append(JqwikDiscoverer.EXAMPLE_SEGMENT_TYPE, methodName);
	}

	public static UniqueId uniqueIdForOverloadedExampleMethod(Class<?> clazz, String methodName, int index) {
		return uniqueIdForExampleMethod(clazz, methodName).append(JqwikDiscoverer.OVERLOADED_SEGMENT_TYPE, String.valueOf(index));
	}

	public static UniqueId engineId() {
		return UniqueId.forEngine(JqwikTestEngine.ENGINE_ID);
	}

}
