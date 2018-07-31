package net.jqwik.api.providers;

import java.math.*;
import java.util.*;
import java.util.stream.Stream;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Size;
import net.jqwik.properties.arbitraries.WildcardArbitrary;
import net.jqwik.providers.RegisteredArbitraryProviders;

import static org.assertj.core.api.Assertions.*;

class RegisteredArbitraryProvidersTests {

	private enum AnEnum {
		One,
		Two,
		Three
	}

	private static class Person {

	}

	@Property
	<T> boolean unconstrainedTypeVariables(@ForAll T aValue) {
		return aValue instanceof WildcardArbitrary.WildcardObject;
	}

	@Property
	boolean objects(@ForAll Object aValue) {
		return aValue != null;
	}

	@Property
	<T> boolean unboundTypeVariable(@ForAll T aValue) {
		return aValue != null;
	}

	@Property
	boolean shortParam(@ForAll short aValue) {
		return true;
	}

	@Property
	boolean shorterParam(@ForAll Short aValue) {
		return aValue != null;
	}

	@Property
	boolean byteParam(@ForAll byte aValue) {
		return true;
	}

	@Property
	boolean intParam(@ForAll int aValue) {
		return true;
	}

	@Property
	boolean byteParam(@ForAll Byte aValue) {
		return aValue != null;
	}

	@Property
	boolean integerParam(@ForAll Integer aValue) {
		return aValue != null;
	}

	@Property
	boolean longParam(@ForAll long aValue) {
		return true;
	}

	@Property
	boolean longerParam(@ForAll Long aValue) {
		return aValue != null;
	}

	@Property
	boolean doubleParam(@ForAll double aValue) {
		return true;
	}

	@Property
	boolean doublerParam(@ForAll Double aValue) {
		return aValue != null;
	}

	@Property
	boolean floatParam(@ForAll float aValue) {
		return true;
	}

	@Property
	boolean floaterParam(@ForAll Float aValue) {
		return aValue != null;
	}

	@Property
	boolean booleanParam(@ForAll boolean aValue) {
		return true;
	}

	@Property
	boolean boxedBooleanParam(@ForAll Boolean aValue) {
		return aValue != null;
	}

	@Property
	boolean charParam(@ForAll char aValue) {
		return true;
	}

	@Property
	boolean boxedCharacterParam(@ForAll Character aValue) {
		return aValue != null;
	}

	@Property
	boolean enumParam(@ForAll AnEnum aValue) {
		return true;
	}

	@Property
	boolean bigIntegerParam(@ForAll BigInteger aValue) {
		return aValue != null;
	}

	@Property
	boolean bigDecimalParam(@ForAll BigDecimal aValue) {
		return aValue != null;
	}

	@Property
	boolean stringParam(@ForAll String aValue) {
		return aValue != null;
	}

	@Property
	boolean integerList(@ForAll List<Integer> aValue) {
		return aValue != null;
	}

	@Property
	void listsAreMutable(@ForAll List<Integer> aValue) {
		aValue.add(41);
	}

	@Property
	boolean integerArray(@ForAll Integer[] aValue) {
		return aValue != null;
	}

	@Property
	boolean integerSet(@ForAll Set<Integer> aValue) {
		return aValue != null;
	}

	@Property
	void setsAreMutable(@ForAll Set<Integer> aValue) {
		aValue.add(42);
	}

	@Property
	boolean collectionsCanBeListsOrSets(@ForAll Collection<Integer> aValue) {
		return (aValue instanceof Set) || (aValue instanceof List);
	}

	@Property
	void collectionsDoVaryOverTheirParameterType(@ForAll @Size(5) Collection<Number> aCollection) {
		Class<?> elementType = aCollection.iterator().next().getClass();
		//Statistics.collect(aCollection.getClass(), elementType);
		assertThat(elementType)
			.isIn(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigDecimal.class, BigInteger.class);
		assertThat(aCollection.stream()).allMatch(element -> element.getClass() == elementType);
	}

	@Property
	boolean rawCollectionsUseObjectAsElementType(@ForAll @Size(1) Collection aCollections) {
		assertThat(aCollections.iterator().next().getClass()).isSameAs(Object.class);
		return (aCollections instanceof Set) || (aCollections instanceof List);
	}

	@Property
	boolean integerStream(@ForAll Stream<Integer> aValue) {
		return aValue != null;
	}

	@Property
	boolean integerOptional(@ForAll Optional<Integer> aValue) {
		return aValue != null;
	}

	@Property
	boolean random(@ForAll Random aValue) {
		return aValue != null;
	}

	@Property
	boolean byteArray(@ForAll byte[] aValue) {
		return aValue != null;
	}

	@Group
	class Registration implements AutoCloseable {

		final ArbitraryProvider personProvider;

		Registration() {
			personProvider = new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isOfType(Person.class);
				}

				@Override
				public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
					return Collections.singleton(Arbitraries.of(new Person()));
				}
			};
			RegisteredArbitraryProviders.register(personProvider);
		}

		@Property
		boolean registeredProviderIsUsedInProperty(@ForAll Person aPerson) {
			return aPerson != null;
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
	}
}
