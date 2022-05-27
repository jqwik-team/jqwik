package net.jqwik.api.domains;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static net.jqwik.engine.support.JqwikCollectors.*;

import static org.assertj.core.api.Assertions.*;

@Group
@PropertyDefaults(tries = 5)
@Domain(DomainInitializationTests.DomainWithInitializeMethod.class)
class DomainInitializationTests {
	@Property
	void prop1(@ForAll int anInt) {
		assertThat(anInt).isEqualTo(42);
	}

	@Property
	void prop2(@ForAll int anInt) {
		assertThat(anInt).isEqualTo(42);
	}

	@Property
	void prop3(@ForAll int anInt) {
		assertThat(anInt).isEqualTo(42);
	}

	@AfterContainer
	static void afterContainer() {
		List<PropertyLifecycleContext> contexts = DomainWithInitializeMethod.initializedContexts;
		assertThat(contexts).hasSize(3);

		Set<String> labels = contexts.stream().map(LifecycleContext::label).collect(toLinkedHashSet());
		assertThat(labels).containsExactlyInAnyOrder("prop1", "prop2", "prop3");
	}

	static class DomainWithInitializeMethod extends DomainContextBase {

		static List<PropertyLifecycleContext> initializedContexts = new ArrayList<>();

		@Override
		public void initialize(PropertyLifecycleContext context) {
			initializedContexts.add(context);
		}

		@Provide
		Arbitrary<Integer> anInt() {
			return Arbitraries.just(42);
		}
	}
}
