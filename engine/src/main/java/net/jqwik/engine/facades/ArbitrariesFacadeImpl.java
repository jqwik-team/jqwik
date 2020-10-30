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
	public <T> EdgeCases<T> edgeCasesChoose(List<T> values) {
		return EdgeCasesSupport.choose(values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EdgeCases<Character> edgeCasesChoose(final char[] characters) {
		List<Character> validCharacters = new ArrayList<>(characters.length);
		for (char character : characters) {
			validCharacters.add(character);
		}
		return edgeCasesChoose(validCharacters);
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
	public <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values, long maxNumberOfSamples) {
		return ExhaustiveGenerators.shuffle(values, maxNumberOfSamples);
	}

	@Override
	public <M> ActionSequenceArbitrary<M> sequences(Arbitrary<? extends Action<M>> actionArbitrary) {
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
	public Arbitrary<String> emails() {
		Arbitrary<String> arbitraryLocalPart = emailsLocalPart();
		Arbitrary<String> arbitraryDomain = emailsDomain();
		return Combinators.combine(arbitraryLocalPart, arbitraryDomain).as((localPart, domain) -> localPart + "@" + domain);
	}

	private static Arbitrary<String> emailsLocalPart(){
		Arbitrary<String> unquoted = emailsLocalPartUnquoted();
		Arbitrary<String> quoted = emailsLocalPartQuoted();
		return Arbitraries.oneOf(unquoted, quoted);
	}

	private static Arbitrary<String> emailsLocalPartUnquoted(){
		Arbitrary<String> unquoted = Arbitraries.strings().alpha().numeric().withChars("!#$%&'*+-/=?^_`{|}~.").ofMinLength(1).ofMaxLength(64);
		unquoted = unquoted.filter(v -> !v.contains(".."));
		unquoted = unquoted.filter(v -> v.charAt(0) != '.');
		unquoted = unquoted.filter(v -> v.charAt(v.length() - 1) != '.');
		return unquoted;
	}

	private static Arbitrary<String> emailsLocalPartQuoted(){
		Arbitrary<String> quoted = Arbitraries.strings().alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]").ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted;
	}

	private static Arbitrary<String> emailsDomain(){
		return Arbitraries.frequencyOf(
				Tuple.of(1, emailsDomainIPv4()),
				Tuple.of(1, emailsDomainIPv6()),
				Tuple.of(2, emailsDomainDomain())
		);
	}

	private static Arbitrary<String> emailsDomainIPv4(){
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart).as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]");
	}

	private static Arbitrary<String> emailsDomainIPv6(){
		//Arbitrary<String> addressPart = Arbitraries.strings().numeric().withChars('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F').ofMinLength(1).ofMaxLength(4);
		//Arbitrary<String> address = Combinators.combine(addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart).as((a, b, c, d, e, f, g, h) -> "[" + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f + ":" + g + ":" + h + "]");
		Arbitrary<String> address = Arbitraries.strings().numeric().alpha().ofLength(10);
		return address;
	}

	private static Arbitrary<String> emailsDomainDomain(){
		Arbitrary<String> domain = Arbitraries.strings().numeric().alpha().ofLength(10);
		return domain;
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
		int hashIdentifier = 0;
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
			hashIdentifier = optionalHash.orElse(0);
		}
		return hashIdentifier;
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
	public <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		// The map cannot be larger than the max number of possible keys
		return new DefaultMapArbitrary<>(keysArbitrary, valuesArbitrary)
				   .ofMaxSize(maxNumberOfElements(keysArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return Combinators.combine(keysArbitrary, valuesArbitrary).as(AbstractMap.SimpleEntry::new);
	}

	private static Set<Arbitrary<?>> allDefaultsFor(TypeUsage typeUsage) {
		DomainContext domainContext = DomainContextFacadeImpl.getCurrentContext();
		RegisteredArbitraryResolver defaultArbitraryResolver =
			new RegisteredArbitraryResolver(domainContext.getArbitraryProviders());
		ArbitraryProvider.SubtypeProvider subtypeProvider = ArbitrariesFacadeImpl::allDefaultsFor;
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
