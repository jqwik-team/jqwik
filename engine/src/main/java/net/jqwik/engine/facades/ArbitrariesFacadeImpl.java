package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
	public <T> EdgeCases<T> edgeCasesChoose(List<T> values, int maxEdgeCases) {
		return EdgeCasesSupport.choose(values, maxEdgeCases);
	}

	@Override
	public EdgeCases<Character> edgeCasesChoose(char[] characters, int maxEdgeCases) {
		List<Character> validCharacters = new ArrayList<>(characters.length);
		for (char character : characters) {
			validCharacters.add(character);
		}
		return edgeCasesChoose(validCharacters, maxEdgeCases);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(List<T> values, long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(values, maxNumberOfSamples);
	}

	@Override
	public RandomGenerator<Character> randomChoose(char[] values) {
		return RandomGenerators.choose(values);
	}

	@Override
	public Optional<ExhaustiveGenerator<Character>> exhaustiveChoose(char[] values, long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(values, maxNumberOfSamples);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<T>> exhaustiveCreate(Supplier<T> supplier, long maxNumberOfSamples) {
		return ExhaustiveGenerators.create(supplier, maxNumberOfSamples);
	}

	@Override
	public <T> Arbitrary<T> oneOf(Collection<Arbitrary<? extends T>> choices) {
		return new OneOfArbitrary<>(choices);
	}

	@Override
	public <T> RandomGenerator<T> randomFrequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		return RandomGenerators.frequency(frequencies);
	}

	@Override
	public <T> RandomGenerator<List<T>> randomShuffle(List<T> values) {
		return RandomGenerators.shuffle(values);
	}

	@Override
	public <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values, long maxNumberOfSamples) {
		return ExhaustiveGenerators.shuffle(values, maxNumberOfSamples);
	}

	@Override
	public <M> ActionSequenceArbitrary<M> sequences(Arbitrary<? extends Action<M>> actionArbitrary) {
		return new DefaultActionSequenceArbitrary<>(actionArbitrary);
	}

	@Override
	public <T> Arbitrary<T> frequencyOf(List<Tuple.Tuple2<Integer, Arbitrary<T>>> frequencies) {
		List<Tuple.Tuple2<Integer, Arbitrary<T>>> aboveZeroFrequencies =
			frequencies.stream().filter(f -> f.get1() > 0).collect(Collectors.toList());
		if (aboveZeroFrequencies.size() == 1) {
			return aboveZeroFrequencies.get(0).get2();
		}
		return new FrequencyOfArbitrary<>(aboveZeroFrequencies);
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
	public <T> Arbitrary<T> lazyOf(List<Supplier<Arbitrary<T>>> suppliers) {
		int hashIdentifier = calculateIdentifier(suppliers.size());
		return LazyOfArbitrary.of(hashIdentifier, suppliers);
	}

	/**
	 * The calculated cash is supposed to be the same for the same callers of Arbitraries.lazyOf()
	 * This is important to have a single instance of LazyOfArbitrary for the same code.
	 */
	private static int calculateIdentifier(int numberOfSuppliers) {
		try {
			throw new RuntimeException();
		} catch (RuntimeException rte) {
			Optional<Integer> optionalHash =
				Arrays.stream(rte.getStackTrace())
					  .filter(stackTraceElement -> !stackTraceElement.getClassName().equals(ArbitrariesFacadeImpl.class.getName()))
					  .filter(stackTraceElement -> !stackTraceElement.getClassName().equals(Arbitraries.class.getName()))
					  .findFirst()
					  .map(stackTraceElement -> Objects.hash(
						  stackTraceElement.getClassName(),
						  stackTraceElement.getMethodName(),
						  stackTraceElement.getLineNumber(),
						  numberOfSuppliers
					  ));
			return optionalHash.orElse(0);
		}
	}

	@Override
	public <T> Arbitrary<T> defaultFor(Class<T> type, Class<?>[] typeParameters) {
		TypeUsage[] genericTypeParameters =
			Arrays.stream(typeParameters)
				  .map(TypeUsage::of)
				  .toArray(TypeUsage[]::new);
		return defaultFor(TypeUsage.of(type, genericTypeParameters), typeUsage -> {throw new CannotFindArbitraryException(typeUsage);});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Arbitrary<T> defaultFor(TypeUsage typeUsage, Function<TypeUsage, Arbitrary<Object>> noDefaultResolver) {
		Set<Arbitrary<?>> arbitraries = allDefaultsFor(typeUsage, noDefaultResolver);
		if (arbitraries.isEmpty()) {
			return (Arbitrary<T>) noDefaultResolver.apply(typeUsage);
		}

		List<Arbitrary<? extends T>> arbitrariesList = new ArrayList<>();
		arbitraries.forEach(arbitrary -> arbitrariesList.add((Arbitrary<? extends T>) arbitrary));
		return Arbitraries.oneOf(arbitrariesList);
	}

	@Override
	public <T> TypeArbitrary<T> forType(Class<T> targetType) {
		return new DefaultTypeArbitrary<>(targetType);
	}

	@Override
	public <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		// The map cannot be larger than the max number of possible keys
		return new DefaultMapArbitrary<>(keysArbitrary, valuesArbitrary)
			.ofMaxSize(maxNumberOfElements(keysArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return Combinators.combine(keysArbitrary, valuesArbitrary).as(AbstractMap.SimpleEntry::new);
	}

	private static Set<Arbitrary<?>> allDefaultsFor(TypeUsage typeUsage, Function<TypeUsage, Arbitrary<Object>> noDefaultResolver) {
		DomainContext domainContext = DomainContextFacadeImpl.getCurrentContext();
		Set<Arbitrary<?>> unconfiguredArbitraries = createDefaultArbitraries(typeUsage, noDefaultResolver, domainContext);
		return configureDefaultArbitraries(typeUsage, domainContext, unconfiguredArbitraries);
	}

	private static Set<Arbitrary<?>> configureDefaultArbitraries(
		TypeUsage typeUsage,
		DomainContext domainContext,
		Set<Arbitrary<?>> unconfiguredArbitraries
	) {
		RegisteredArbitraryConfigurer defaultArbitraryConfigurer = new RegisteredArbitraryConfigurer(domainContext.getArbitraryConfigurators());
		return unconfiguredArbitraries.stream()
									  .map(arbitrary -> defaultArbitraryConfigurer.configure(arbitrary, typeUsage))
									  .collect(Collectors.toSet());
	}

	private static Set<Arbitrary<?>> createDefaultArbitraries(
		TypeUsage typeUsage,
		Function<TypeUsage, Arbitrary<Object>> noDefaultResolver,
		DomainContext domainContext
	) {
		RegisteredArbitraryResolver defaultArbitraryResolver = new RegisteredArbitraryResolver(domainContext.getArbitraryProviders());
		ArbitraryProvider.SubtypeProvider subtypeProvider = subtypeUsage -> {
			Set<Arbitrary<?>> subtypeArbitraries = allDefaultsFor(subtypeUsage, noDefaultResolver);
			if (subtypeArbitraries.isEmpty()) {
				return Collections.singleton(noDefaultResolver.apply(subtypeUsage));
			}
			return subtypeArbitraries;
		};
		return defaultArbitraryResolver.resolve(typeUsage, subtypeProvider);
	}

	@Override
	public <T> Arbitrary<T> recursive(
		Supplier<Arbitrary<T>> base,
		Function<Arbitrary<T>, Arbitrary<T>> recur,
		int depth
	) {
		if (depth == 0) {
			return base.get();
		}
		return recur.apply(recursive(base, recur, depth - 1));
	}
}
