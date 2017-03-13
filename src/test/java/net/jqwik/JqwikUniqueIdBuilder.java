
package net.jqwik;

import net.jqwik.discovery.ContainerClassDescriptor;
import net.jqwik.discovery.ExampleMethodDescriptor;
import org.junit.platform.engine.UniqueId;

public class JqwikUniqueIdBuilder {

	public static UniqueId uniqueIdForClassContainer(Class<?> clazz) {
		return engineId().append(ContainerClassDescriptor.SEGMENT_TYPE, clazz.getName());
	}

	public static UniqueId uniqueIdForExampleMethod(Class<?> clazz, String methodName) {
		return uniqueIdForClassContainer(clazz).append(ExampleMethodDescriptor.SEGMENT_TYPE, methodName);
	}

	public static UniqueId engineId() {
		return UniqueId.forEngine(JqwikTestEngine.ENGINE_ID);
	}

}
