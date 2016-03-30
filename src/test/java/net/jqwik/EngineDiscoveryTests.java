
package net.jqwik;

import static org.junit.gen5.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import com.pholser.junit.quickcheck.Property;
import org.junit.gen5.api.Test;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.discovery.MethodSelector;
import org.junit.gen5.engine.discovery.UniqueIdSelector;
import org.junit.gen5.launcher.main.TestDiscoveryRequestBuilder;

class EngineDiscoveryTests extends AbstractEngineTests {

	@Test
	void discoverClass() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			ClassSelector.forClass(MyProperties.class)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		List<UniqueId> uniqueIds = getUniqueIds(engineDescriptor);

		assertEquals(4, uniqueIds.size());
		Class<MyProperties> myPropertiesClass = MyProperties.class;
		assertTrue(uniqueIds.contains(uniqueIdForClass(myPropertiesClass)));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "booleanProperty"));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "voidProperty"));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "staticProperty"));
	}

	@Test
	void discoverClassByUniqueId() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
				UniqueIdSelector.forUniqueId(uniqueIdForClass(MyProperties.class))).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		List<UniqueId> uniqueIds = getUniqueIds(engineDescriptor);

		assertEquals(4, uniqueIds.size());
		Class<MyProperties> myPropertiesClass = MyProperties.class;
		assertTrue(uniqueIds.contains(uniqueIdForClass(MyProperties.class)));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "booleanProperty"));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "voidProperty"));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "staticProperty"));
	}

	@Test
	void discoverMethod() {
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
			MethodSelector.forMethod(MyProperties.class, "booleanProperty")).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		List<UniqueId> uniqueIds = getUniqueIds(engineDescriptor);

		assertEquals(2, uniqueIds.size());
		Class<MyProperties> myPropertiesClass = MyProperties.class;
		assertTrue(uniqueIds.contains(uniqueIdForClass(myPropertiesClass)));
		containsMatchingId(uniqueIds, uniqueIdForMethod(myPropertiesClass, "booleanProperty"));
	}

	@Test
	void discoverMethodByUniqueWithoutSeed() {
		UniqueId methodUniqueId = uniqueIdForMethod(MyProperties.class, "booleanProperty");
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
				UniqueIdSelector.forUniqueId(methodUniqueId)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		List<UniqueId> uniqueIds = getUniqueIds(engineDescriptor);

		assertEquals(2, uniqueIds.size());
		assertTrue(uniqueIds.contains(uniqueIdForClass(MyProperties.class)));
		containsMatchingId(uniqueIds, methodUniqueId);
	}

	@Test
	void discoverMethodByUniqueWithSeed() {
		UniqueId uniqueIdWithSeed = uniqueIdForMethodAndSeed(MyProperties.class, "booleanProperty", "1");
		EngineDiscoveryRequest discoveryRequest = TestDiscoveryRequestBuilder.request().select(
				UniqueIdSelector.forUniqueId(uniqueIdWithSeed)).build();
		TestDescriptor engineDescriptor = engine.discover(discoveryRequest, UniqueId.forEngine(engine.getId()));

		List<UniqueId> uniqueIds = getUniqueIds(engineDescriptor);

		assertEquals(2, uniqueIds.size());
		Class<MyProperties> myPropertiesClass = MyProperties.class;
		assertTrue(uniqueIds.contains(uniqueIdForClass(myPropertiesClass)));
		assertTrue(uniqueIds.contains(uniqueIdWithSeed));
	}

	private void containsMatchingId(List<UniqueId> uniqueIds, UniqueId uniqueId) {
		assertTrue(uniqueIds.stream().anyMatch(id -> id.getUniqueString().startsWith(uniqueId.getUniqueString())));
	}

	private List<UniqueId> getUniqueIds(TestDescriptor engineDescriptor) {
		return engineDescriptor.allDescendants().stream().map(d -> d.getUniqueId()).collect(Collectors.toList());
	}

	static class MyProperties {
		@Property
		private void skipBecausePrivate() {
		}

		@Property
		boolean booleanProperty() {
			return true;
		}

		@Property
		void voidProperty() {
		}

		@Property
		static void staticProperty() {
		}
	}

}
