package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.stateful.*;

import static net.jqwik.engine.properties.arbitraries.ArbitrariesSupport.*;

/**
 * Is loaded through reflection in api module
 */
public class ArbitrariesFacadeImpl extends Arbitraries.ArbitrariesFacade {
	@Override
	public <T> RandomGenerator<T> randomChoose(List<T> values) {
		return RandomGenerators.choose(values);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(List<T> values) {
		return ExhaustiveGenerators.choose(values);
	}

	@Override
	public RandomGenerator<Character> randomChoose(char[] values) {
		return RandomGenerators.choose(values);
	}

	@Override
	public Optional<ExhaustiveGenerator<Character>> exhaustiveChoose(char[] values) {
		return ExhaustiveGenerators.choose(values);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<T>> exhaustiveCreate(Supplier<T> supplier) {
		return ExhaustiveGenerators.create(supplier);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Enum> RandomGenerator<T> randomChoose(Class<T> enumClass) {
		return RandomGenerators.choose(enumClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Enum> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(Class<T> enumClass) {
		return ExhaustiveGenerators.choose(enumClass);
	}

	@Override
	public <T> Arbitrary<T> oneOf(List<Arbitrary<T>> all) {
		return new OneOfArbitrary<>(all);
	}

	@Override
	public <T> RandomGenerator<T> randomFrequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		return RandomGenerators.frequency(frequencies);
	}

	@Override
	public <T> RandomGenerator<T> randomSamples(T[] samples) {
		return RandomGenerators.samples(samples);
	}

	@Override
	public <T> RandomGenerator<List<T>> randomShuffle(List<T> values) {
		return RandomGenerators.shuffle(values);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values) {
		return ExhaustiveGenerators.shuffle(values);
	}

	@Override
	public <M> ActionSequenceArbitrary<M> sequences(Arbitrary<Action<M>> actionArbitrary) {
		return new DefaultActionSequenceArbitrary<>(actionArbitrary);
	}

	@Override
	public <T> Arbitrary<T> frequencyOf(List<Tuple.Tuple2<Integer, Arbitrary<T>>> frequencies) {
		return new FrequencyOfArbitrary<>(frequencies);
	}

	@Override
	public IntegerArbitrary integers() {
		return new DefaultIntegerArbitrary();
	}

	@Override
	public LongArbitrary longs() {
		return new DefaultLongArbitrary();
	}

	@Override
	public BigIntegerArbitrary bigIntegers() {
		return new DefaultBigIntegerArbitrary();
	}

	@Override
	public FloatArbitrary floats() {
		return new DefaultFloatArbitrary();
	}

	@Override
	public BigDecimalArbitrary bigDecimals() {
		return new DefaultBigDecimalArbitrary();
	}

	@Override
	public DoubleArbitrary doubles() {
		return new DefaultDoubleArbitrary();
	}

	@Override
	public ByteArbitrary bytes() {
		return new DefaultByteArbitrary();
	}

	@Override
	public ShortArbitrary shorts() {
		return new DefaultShortArbitrary();
	}

	@Override
	public StringArbitrary strings() {
		return new DefaultStringArbitrary();
	}

	@Override
	public CharacterArbitrary chars() {
		return new DefaultCharacterArbitrary();
	}

	@Override
	public <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier) {
		return new LazyArbitrary<>(arbitrarySupplier);
	}

	@Override
	public <T> Arbitrary<T> defaultFor(Class<T> type, Class<?>[] typeParameters) {
		TypeUsage[] genericTypeParameters =
			Arrays.stream(typeParameters)
				  .map(TypeUsage::of)
				  .toArray(TypeUsage[]::new);
		return defaultFor(TypeUsage.of(type, genericTypeParameters));
	}

	@Override
	public <T> Arbitrary<T> defaultFor(TypeUsage typeUsage) {
		// Lazy evaluation is necessary since defaults only exist in context of a domain
		// and domains are only present during execution of a property
		return Arbitraries.lazy(() -> {
			Set<Arbitrary<?>> arbitraries = allDefaultsFor(typeUsage);
			if (arbitraries.isEmpty()) {
				throw new CannotFindArbitraryException(typeUsage);
			}

			List<Arbitrary<T>> arbitrariesList = new ArrayList<>();
			//noinspection unchecked
			arbitraries.forEach(arbitrary -> arbitrariesList.add((Arbitrary<T>) arbitrary));
			return Arbitraries.oneOf(arbitrariesList);
		});
	}

	@Override
	public <T> TypeArbitrary<T> forType(Class<T> targetType) {
		return new DefaultTypeArbitrary<>(targetType).useDefaults();
	}

	@Override
	public <K, V> SizableArbitrary<Map<K, V>> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		// The map cannot be larger than the max number of possible keys
		return new MapArbitrary<>(keysArbitrary, valuesArbitrary)
				   .ofMaxSize(maxNumberOfElements(keysArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return Combinators.combine(keysArbitrary, valuesArbitrary).as(AbstractMap.SimpleEntry::new);
	}

	private static Set<Arbitrary<?>> allDefaultsFor(TypeUsage typeUsage) {
		DomainContext domainContext = DomainContextFacadeImpl.currentContext.get();
		RegisteredArbitraryResolver defaultArbitraryResolver =
			new RegisteredArbitraryResolver(domainContext.getArbitraryProviders());
		ArbitraryProvider.SubtypeProvider subtypeProvider = ArbitrariesFacadeImpl::allDefaultsFor;
		return defaultArbitraryResolver.resolve(typeUsage, subtypeProvider);
	}

}
