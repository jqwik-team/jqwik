package net.jqwik.api.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.providers.*;

@Group
class ProgrammaticArbitraryProviderRegistrationTests implements AutoCloseable {

	final ArbitraryProvider personProvider;

	ProgrammaticArbitraryProviderRegistrationTests() {
		personProvider = new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(TypeUsage targetType) {
				return targetType.isOfType(MyDomainClass.class);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				return Collections.singleton(Arbitraries.of(new MyDomainClass("stranger")));
			}
		};
		RegisteredArbitraryProviders.register(personProvider);
	}

	@Property
	boolean registeredProviderIsUsedInProperty(@ForAll MyDomainClass anObject) {
		return anObject != null && anObject.name.equals("stranger");
	}

	@Example
	boolean manuallyRegisteredProviderIsPartOfDefaultProviders() {
		return RegisteredArbitraryProviders.getProviders().contains(personProvider);
	}

	@Example
	boolean manuallyRegisteredProviderCanBeUnregistered() {
		RegisteredArbitraryProviders.unregister(personProvider);
		return !RegisteredArbitraryProviders.getProviders().contains(personProvider);
	}

	@Example
	boolean manuallyRegisteredProviderCanBeUnregisteredByClass() {
		RegisteredArbitraryProviders.unregister(personProvider.getClass());
		return !RegisteredArbitraryProviders.getProviders().contains(personProvider);
	}

	@Override
	public void close() {
		RegisteredArbitraryProviders.unregister(personProvider);
	}

	private static class MyDomainClass {
		String name;

		public MyDomainClass(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
