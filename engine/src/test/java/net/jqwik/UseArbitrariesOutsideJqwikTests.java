package net.jqwik;

import java.util.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.sessions.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.providers.*;

import static org.assertj.core.api.Assertions.*;

// These tests must run outside jqwik!
class UseArbitrariesOutsideJqwikTests {

	@Test
	void forType() {
		TypeArbitrary<Person> people = Arbitraries.forType(Person.class);

		people.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value.firstName).isNotNull();
			assertThat(value.lastName).isNotNull();
		});
	}

	@Test
	void defaultFor() {
		Arbitrary<String> strings = Arbitraries.defaultFor(String.class);

		strings.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value).isInstanceOf(String.class);
		});
	}

	@Test
	void defaultForWithGloballyRegisteredProvider() {
		RegisteredArbitraryProviders.register(new PersonProvider());

		Arbitrary<Person> people = Arbitraries.defaultFor(Person.class);

		people.sampleStream().limit(100).forEach(value -> {
			assertThat(value).isNotNull();
			assertThat(value.firstName).isNotNull();
			assertThat(value.lastName).isNotNull();
		});
	}

	@Test
	void forStrings() {
		assertThat(Arbitraries.strings().sample()).isInstanceOf(String.class);
	}

	@Test
	void lazyOf() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);

		Arbitrary<Integer> sum = Arbitraries.lazyOf(
			() -> Arbitraries.just(0),
			() -> ints
		);

		assertThat(sum.sample()).isBetween(-1000, 1000);
	}

	@Test
	void injectDuplicates() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.5);

		Random random = new Random();
		// Try 20 times because it fails sometimes due to enhanced probability of no duplicates
		for (int i = 0; i < 20; i++) {

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
			Set<Integer> noMoreDuplicates = new LinkedHashSet<>(listWithDuplicates);

			if (i < 19) {
				// Can fail because collections with no duplicates have probability of approx. 5%
				if (noMoreDuplicates.size() < 80) {
					return;
				}
			} else {
				assertThat(noMoreDuplicates).hasSizeLessThanOrEqualTo(65);
			}
		}
	}

	@Test
	void samplingOfSameArbitraryShouldUseSameGenerator() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(1.0);

		int i1 = intsWithDuplicates.sample();
		int i2 = intsWithDuplicates.sample();
		int i3 = intsWithDuplicates.sample();
		int i4 = intsWithDuplicates.sample();

		assertThat(Arrays.asList(i1, i2, i3, i4)).allMatch(i -> i == i1);
	}

	private static class Person {
		private final String firstName;
		private final String lastName;

		public Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public static Person create(String firstName) {
			return new Person(firstName, "Stranger");
		}
	}

	/**
	 * Has been brought up in https://github.com/jlink/jqwik/issues/205
	 */
	@Test
	void useSampleInArbitraryDefinition() {
		// BTW: You should never do this. Using sample() within another generator calls for flatMap or combine
		Arbitraries.strings()
				   .map(it -> it + Arbitraries.strings().alpha().sample())
				   .filter(it -> it != null && !it.trim().isEmpty())
				   .sample();
	}

	@Nested
	class Sessions {

		int initialStoreSize = StoreRepository.getCurrent().size();

		@AfterEach
		void finishSession() {
			if (JqwikSession.isActive()) {
				JqwikSession.finish();
			}
		}

		@Test
		void finishingJqwikSessionWillGetRidOfStores() {
			JqwikSession.start();
			assertThat(JqwikSession.isActive()).isTrue();
			Stream<String> stringStream = Arbitraries.strings().ofLength(5).sampleStream();
			stringStream.limit(10).forEach(v -> assertThat(v).hasSize(5));
			JqwikSession.finish();
			assertThat(JqwikSession.isActive()).isFalse();
			assertThat(StoreRepository.getCurrent().size()).isEqualTo(initialStoreSize);
		}

		@Test
		void sessionCannotBeStartedTwice() {
			JqwikSession.start();
			assertThatThrownBy(() -> JqwikSession.start()).isInstanceOf(JqwikException.class);
		}

		@Test
		void finishingTryWillResetTryStores() {
			JqwikSession.start();
			Store<String> stringStore = Store.create("strings", Lifespan.TRY, () -> "initial");
			stringStore.update(s -> s + " changed");
			JqwikSession.finishTry();
			assertThat(stringStore.get()).isEqualTo("initial");
		}

		@Test
		void runInSession() {
			JqwikSession.run(() -> {
				assertThat(JqwikSession.isActive()).isTrue();
				Stream<String> stringStream = Arbitraries.strings().ofLength(5).sampleStream();
				stringStream.limit(10).forEach(v -> assertThat(v).hasSize(5));
			});
			assertThat(JqwikSession.isActive()).isFalse();
			assertThat(StoreRepository.getCurrent().size()).isEqualTo(initialStoreSize);
		}

	}

	private static class PersonProvider implements ArbitraryProvider {
		@Override
		public boolean canProvideFor(TypeUsage targetType) {
			return targetType.isOfType(Person.class);
		}

		@Override
		public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
			return Collections.singleton(Arbitraries.just(new Person("first", "last")));
		}
	}
}
