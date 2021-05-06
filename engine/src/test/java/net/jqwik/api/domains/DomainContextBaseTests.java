package net.jqwik.api.domains;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

import static org.assertj.core.api.Assertions.*;

@Group
class DomainContextBaseTests {

	@Group
	@PropertyDefaults(tries = 20)
	class ArbitraryProviders {

		@Property
		@Domain(ContextWithProviderMethods.class)
		void useProviderFromMethodWithTypedArbitraryInterface(@ForAll char aChar) {
			assertThat(aChar).isBetween('0', '9');
		}

		@Property
		@Domain(ContextWithProviderMethods.class)
		void useProviderFromMethodWithSubtypeOfArbitraryInterface(@ForAll String aString) {
			assertThat(aString).hasSize(2);
			assertThat(aString).containsOnlyDigits();
		}

		@Property
		@Domain(ContextWithProviderMethods.class)
		void useProviderFromMethodWithTargetTypeAndSubtypeProvider(@ForAll List<String> listOfStrings) {
			assertThat(listOfStrings).hasSize(3);
			listOfStrings.forEach(aString -> {
				assertThat(aString).isInstanceOf(String.class);
				assertThat(aString).hasSize(2);
				assertThat(aString).containsOnlyDigits();
			});
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		@Domain(ContextWithProviderMethods.class)
		void useProviderFromMethodWithPotentiallyConflictingType(@ForAll List<LocalDate> listOfDates) {
			assertThat(listOfDates).hasSize(1);
		}

	}

}


class ContextWithProviderMethods extends DomainContextBase {

	@Provide
	StringArbitrary numberStrings() {
		return Arbitraries.strings().numeric().ofLength(2);
	}

	@Provide //("numbers") // having a value will produce warning log entry
	Arbitrary<Character> numberChars() {
		return Arbitraries.integers().between(0, 9).map(i -> Character.forDigit(i, 10));
	}

	@Provide
	ListArbitrary<?> listsOfSize3(
		ArbitraryProvider.SubtypeProvider subtypeProvider,
		TypeUsage targetType
	) {
		TypeUsage innerTarget = targetType.getTypeArgument(0);
		Optional<Arbitrary<?>> optionalInner = subtypeProvider.provideOneFor(innerTarget);
		return optionalInner.map(inner -> inner.list().ofSize(3)).orElse(null);
	}

	@Provide
	Arbitrary<List<LocalDate>> listsOfDates() {
		return Arbitraries.just(LocalDate.now()).list().ofSize(1);
	}

	//@Provide
	String shouldProduceAWarningLogEntry() {
		return "hello";
	}

}

// private NumberStringContext() {
// 	registerConfigurator(new ArbitraryConfiguratorBase() {
// 		public Arbitrary<String> configure(Arbitrary<String> arbitrary, AbstractDomainContextBaseTests.DoubleString ignore) {
// 			return arbitrary.map(s -> s + s);
// 		}
// 	});
// }
