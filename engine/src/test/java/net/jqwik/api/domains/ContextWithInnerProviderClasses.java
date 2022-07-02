package net.jqwik.api.domains;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;

class ContextWithInnerProviderClasses extends DomainContextBase {

	class MyStringProvider implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(String.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.strings().numeric().ofLength(2));
		}
	}

	class ListOfSize3Provider implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(List.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			TypeUsage innerTarget = targetType.getTypeArgument(0);
			Set<Arbitrary<?>> innerProviders = subtypeProvider.apply(innerTarget);
			return innerProviders.stream().map(inner -> inner.list().ofSize(3)).collect(CollectorsSupport.toLinkedHashSet());
		}
	}

	static class StaticArbitraryProvider implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(Boolean.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.just(false));
		}
	}

	private class ShouldNotBeUsedBecausePrivate implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(String.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.strings().alpha().ofLength(42));
		}
	}

	class ShouldNotBeUsedBecauseLowPriority implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(Integer.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.just(414243));
		}

		@Override
		public int priority() {
			return -1000;
		}
	}

/*
	// Will lead to a warning log message
	class ShouldNotBeUsedBecauseNoDefaultConstructor implements ArbitraryProvider {

		ShouldNotBeUsedBecauseNoDefaultConstructor(int ignore) {
		}

		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isAssignableFrom(Integer.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.just(414243));
		}
	}
*/

}
