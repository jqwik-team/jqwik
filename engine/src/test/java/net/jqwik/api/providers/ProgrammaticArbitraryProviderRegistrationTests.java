package net.jqwik.api.providers;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.api.*;
import net.jqwik.engine.providers.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

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

	@Example
	// only can fail if run as only/first test
	void initIsSafe() throws Exception {
		Callable<List<ArbitraryProvider>> lookup = RegisteredArbitraryProviders::getProviders;
		ExecutorService exService = Executors.newFixedThreadPool(2);

		Future<List<ArbitraryProvider>> f1 = exService.submit(lookup);
		Future<List<ArbitraryProvider>> f2 = exService.submit(lookup);

		exService.shutdown();
		exService.awaitTermination(2, SECONDS);

		assertThat(f1.get()).containsExactlyElementsOf(f2.get());
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
