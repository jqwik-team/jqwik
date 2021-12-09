package net.jqwik.api.providers;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.providers.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
@Group
class RegisteredArbitraryProvidersTests {

	private enum AnEnum {
		One,
		Two,
		Three
	}

	@Group
	class Base_Types {

		@Property
		boolean objects(@ForAll Object aValue) {
			return aValue != null;
		}

		@Property
		boolean random(@ForAll Random aValue) {
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

	}

	@Group
	class Type_Variables_and_Wildcards {
		@Property
		<T> boolean unconstrainedTypeVariables(@ForAll T aValue) {
			return aValue != null;
		}

		@Property
		<T extends Comparable<T>> boolean constrainedTypeVariable(@ForAll T aValue) {
			return aValue != null;
		}

		@Property
		<T extends BigInteger> boolean bigIntegerSubtypeParam(@ForAll T aValue) {
			return aValue.getClass().equals(BigInteger.class);
		}

		@Property
		<T extends BigDecimal> boolean bigDecimalSubtypeParam(@ForAll T aValue) {
			return aValue.getClass().equals(BigDecimal.class);
		}

		@Property
		boolean listOfUnconstrainedWildcard(@ForAll List<?> aValue) {
			return aValue.stream().allMatch(e -> e != null);
		}

	}

	@Group
	class ArrayTypes {

		@Property
		boolean integerArray(@ForAll Integer[] aValue) {
			return aValue != null;
		}

		@Property
		boolean intArray(@ForAll int[] aValue) {
			return aValue != null;
		}

		@Property
		boolean byteArray(@ForAll byte[] aValue) {
			return aValue != null;
		}

		@Property
		<T> boolean genericArray(@ForAll T[] aValue) {
			return aValue != null;
		}

		@Property
		<T extends Comparable<T>> boolean genericConstrainedArray(@ForAll T[] aValue) {
			return aValue != null;
		}

		@Property
		@ExpectFailure(failureType = JqwikException.class)
		<T extends Comparable<T> & Serializable> void twoUpperBoundsDoNotWorkInGenericArrays(@ForAll T[] aValue) {
		}
	}

	@Group
	class CollectionTypes {

		@Property
		boolean integerList(@ForAll List<Integer> aValue) {
			return aValue != null;
		}

		@Property
		void listsAreMutable(@ForAll List<Integer> aValue) {
			aValue.add(41);
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

		@Property(tries = 10)
		boolean integerStream(@ForAll Stream<Integer> aValue) {
			assertThat(aValue).isNotNull();
			// Force stream to evaluate
			//noinspection ConstantConditions
			return aValue.count() >= 0;
		}

		@Property
		boolean integerIterable(@ForAll Iterable<Integer> aValue) {
			return aValue instanceof List || aValue instanceof Set;
		}

		@Property
		boolean integerIterator(@ForAll Iterator<Integer> aValue) {
			return aValue != null;
		}

	}

	@Group
	class Other_Generic_Types {

		@Property
		boolean integerOptional(@ForAll Optional<Integer> aValue) {
			return aValue != null;
		}

		@Property(tries = 10)
		boolean mapsOfIntegerAndString(@ForAll Map<Integer, String> aValue) {
			return aValue != null;
		}

		@Property(tries = 10)
		void mapsAreMutable(@ForAll Map<Integer, String> aValue) {
			aValue.put(42, "forty two");
		}

		@Property(tries = 10)
		void hashMaps(@ForAll HashMap<Integer, String> aValue) {
			assertThat(aValue).isInstanceOf(HashMap.class);
		}

		@Property(tries = 50)
		void mapsWithNumberKeysAndNumberValues(@ForAll @Size(2) Map<Number, Number> aMap) {
			assertThat(aMap.keySet()).allMatch(k -> Number.class.isAssignableFrom(k.getClass()));
			assertThat(aMap.values()).allMatch(k -> Number.class.isAssignableFrom(k.getClass()));
			assertThat(aMap).hasSize(2);
		}

		@Property
		boolean entryOfIntegerAndString(@ForAll Map.Entry<Integer, String> aValue) {
			return aValue != null;
		}

		@Property
		void entriesAreMutable(@ForAll Map.Entry<Integer, String> anEntry) {
			anEntry.setValue("other");
		}
	}

	private interface MyConcreteFunction extends Function<Integer, String> {}

	private interface MyPartialFunction1<T> extends Function<T, String> {}

	private interface MyPartialFunction2<S> extends Function<Integer, S> {}

	private interface MyFunction<P, R> {
		R apply(P p);
	}

	private interface MySpecial<P, R> {
		R apply(P p);
	}

	@Group
	class Functions_and_SAM_types implements AutoCloseable {

		private final ArbitraryProvider specialProvider;

		Functions_and_SAM_types() {
			specialProvider = new ArbitraryProvider() {
				@Override
				public boolean canProvideFor(TypeUsage targetType) {
					return targetType.isOfType(MySpecial.class);
				}

				@Override
				public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
					Arbitrary<MySpecial<Integer, String>> constant = Arbitraries.just(integer -> "special");
					return Collections.singleton(constant);
				}
			};
			RegisteredArbitraryProviders.register(specialProvider);
		}

		@Override
		public void close() {
			RegisteredArbitraryProviders.unregister(specialProvider);
		}

		@Property
		void registeredProvidersHavePriority(@ForAll MySpecial<Integer, String> aFunction) {
			assertThat(aFunction.apply(3)).isEqualTo("special");
		}

		@Property
		void simpleFunction(@ForAll Function<Integer, @StringLength(5) String> aFunction) {
			String value = aFunction.apply(3);
			assertThat(value).isInstanceOf(String.class);
			assertThat(value).hasSize(5);
		}

		@Property
		void selfDefinedFunction(@ForAll MyFunction<Integer, @StringLength(5) String> aFunction) {
			String value = aFunction.apply(3);
			assertThat(value).isInstanceOf(String.class);

			// TODO: Retest with newer JDK
			//  Does not work because of bug in JDK which handles annotations of inner interfaces
			//  differently than annotations of top level interfaces
			// assertThat(value).hasSize(5);
		}

		@Property
		void concreteFunction(@ForAll MyConcreteFunction aFunction) {
			assertThat(aFunction.apply(3)).isInstanceOf(String.class);
		}

		@Property
		void partialFunctions(
			@ForAll MyPartialFunction1<Integer> function1,
			@ForAll MyPartialFunction2<String> function2
		) {
			assertThat(function1.apply(3)).isInstanceOf(String.class);
			assertThat(function2.apply(3)).isInstanceOf(String.class);
		}

		@Property
		<R extends Serializable> void typeVariablesKeepTheirUpperBound(
			@ForAll Function<Integer, R> function
		) {
			assertThat(function.apply(3)).isInstanceOf(Serializable.class);
		}

		@Property
		void wildcardsKeepTheirLowerBound(
			@ForAll Function<Integer, ? super String> function
		) {
			assertThat(function.apply(3)).isInstanceOf(String.class);
		}

		@Property
		@Label("java.function.Predicate")
		void predicate(@ForAll Predicate<Integer> aPredicate) {
			assertThat(aPredicate.test(3)).isInstanceOf(Boolean.class);
		}

		@Property
		@Label("java.function.Consumer")
		void consumer(@ForAll Consumer<String> aConsumer) {
			aConsumer.accept("anything");
		}

		@Property
		@Label("java.function.Supplier")
		void supplier(@ForAll Supplier<String> aSupplier) {
			assertThat(aSupplier.get()).isInstanceOf(String.class);
		}

	}

}
