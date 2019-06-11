package net.jqwik.api.providers;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;
import net.jqwik.engine.properties.arbitraries.WildcardArbitrary.*;
import net.jqwik.engine.providers.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.constraints.UseTypeMode.*;

class RegisteredArbitraryProvidersTests {

	private enum AnEnum {
		One,
		Two,
		Three
	}

	private static class Person {
		private String name;

		public static Person create(String name) {
			return new Person("factory: " + name);
		}

		private static Person createPrivate(String name) {
			return new Person("private factory: " + name);
		}

		public Person(String name) {
			this.name = name;
		}

		private Person(String name, int ignore) {
			this("private ctor: " + name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Property
	boolean objects(@ForAll Object aValue) {
		return aValue != null;
	}

	@Property
	<T> boolean unconstrainedTypeVariables(@ForAll T aValue) {
		return aValue != null;
	}

	@Property
	<T extends Comparable> boolean constrainedTypeVariable(@ForAll T aValue) {
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
	<T extends BigInteger> boolean bigIntegerSubtypeParam(@ForAll T aValue) {
		return aValue.getClass().equals(BigInteger.class);
	}

	@Property
	boolean bigDecimalParam(@ForAll BigDecimal aValue) {
		return aValue != null;
	}

	@Property
	<T extends BigDecimal> boolean bigDecimalSubtypeParam(@ForAll T aValue) {
		return aValue.getClass().equals(BigDecimal.class);
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
	boolean mapsOfIntegerAndString(@ForAll Map<Integer, String> aValue) {
		return aValue != null;
	}

	@Property
	void mapsAreMutable(@ForAll Map<Integer, String> aValue) {
		aValue.put(42, "forty two");
	}

	@Property(tries = 50)
	void mapsWithNumberKeysAndNumberValues(@ForAll @Size(2) Map<Number, Number> aMap) {
		assertThat(aMap.keySet()).allMatch(k -> Number.class.isAssignableFrom(k.getClass()));
		assertThat(aMap.values()).allMatch(k -> Number.class.isAssignableFrom(k.getClass()));
	}

	@Property
	boolean listOfUnconstrainedWildcard(@ForAll List<?> aValue) {
		return aValue.stream().allMatch(e -> e instanceof WildcardObject);
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
	boolean integerIterable(@ForAll Iterable<Integer> aValue) {
		return aValue instanceof List || aValue instanceof Set;
	}

	@Property
	boolean integerIterator(@ForAll Iterator<Integer> aValue) {
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
	@Label("@UseType")
	class UseTypeTests {
		@Property
		boolean withoutValue(@ForAll @UseType Person aPerson) {
			return aPerson != null;
		}

		@Property
		boolean fromAllConstructorsAndFactories(@ForAll @UseType({CONSTRUCTORS, FACTORIES}) Person aPerson) {
			return aPerson != null;
		}

		@Property(tries = 20)
		@Domain(SmallNumbers.class)
		void useTypeShouldWorkRegardlessOfDomainContext(
			@ForAll int smallNumber,
			@ForAll @UseType Random random
		) {
			assertThat(smallNumber).isBetween(1, 99);
			assertThat(random).isNotNull();
		}

		class SmallNumbers extends AbstractDomainContextBase {
			public SmallNumbers() {
				registerArbitrary(int.class, Arbitraries.integers().between(1, 99));
				registerArbitrary(long.class, Arbitraries.longs());
			}
		}

	}

	@Group
	class ProgrammaticRegistration implements AutoCloseable {

		final ArbitraryProvider personProvider;

		ProgrammaticRegistration() {
			personProvider = new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isOfType(Person.class);
				}

				@Override
				public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
					return Collections.singleton(Arbitraries.of(new Person("stranger")));
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
