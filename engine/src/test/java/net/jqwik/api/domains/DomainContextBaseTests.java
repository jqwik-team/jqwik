package net.jqwik.api.domains;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@PropertyDefaults(tries = 20)
class DomainContextBaseTests {

	@Group
	class ArbitraryProviderMethods {

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
		void useProviderFromMethodWithCovariantOfArbitraryInterface(@ForAll CharSequence charSequence) {
			assertThat(charSequence).hasSize(2);
			assertThat(charSequence).containsOnlyDigits();
		}

		@Property
		@Domain(ContextWithProviderMethods.class)
		void useProviderFromMethodWithTargetType(@ForAll List<String> listOfStrings) {
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

		@Property
		@Domain(ContextWithDependentProviders.class)
		void flatmapOverInjectedForAllParameter(@ForAll List<Integer> listOf5Ints) {
			assertThat(listOf5Ints).hasSize(5);
			int first = listOf5Ints.get(0);
			assertThat(first).isBetween(1, 10);
			assertThat(listOf5Ints).allMatch(i -> i == first);
		}

		@Property
		@Domain(ContextWithDependentProviders.class)
		void flatmapOverInjectedForAllParameterWithValue(@ForAll String aString) {
			assertThat(aString).isEqualTo("aa");
		}

		@Property
		@Domain(ContextWithDependentProviders.class)
		void flatmapOverInjectedForAllParameterWithSupplier(@ForAll double value) {
			assertThat(value).isGreaterThanOrEqualTo(10.0);
		}
	}

	@Group
	class WithArbitraryProviderClasses {

		@Property
		@Domain(ContextWithInnerProviderClasses.class)
		void useProviderFromInnerClass(@ForAll String aString) {
			assertThat(aString).hasSize(2);
			assertThat(aString).containsOnlyDigits();
		}

		@Property
		@Domain(ContextIsArbitraryProvider.class)
		void useContextItselfAsArbitraryProvider(@ForAll String aString) {
			assertThat(aString).hasSize(2);
			assertThat(aString).containsOnlyDigits();
		}

		@Property
		@Domain(ContextWithInnerProviderClasses.class)
		void useProviderFromStaticInnerClass(@ForAll boolean aBoolean) {
			assertThat(aBoolean).isFalse();
		}

		@Property
		@Domain(ContextWithInnerProviderClasses.class)
		void useGenericProviderFromInnerClass(@ForAll List<String> listOfStrings) {
			assertThat(listOfStrings).hasSize(3);
			listOfStrings.forEach(aString -> {
				assertThat(aString).isInstanceOf(String.class);
				assertThat(aString).hasSize(2);
				assertThat(aString).containsOnlyDigits();
			});
		}

		@Property
		@Domain(ContextWithInnerProviderClasses.class)
		@Domain(DomainContext.Global.class)
		void dontUseProviderWithPriorityLowerThanDefault(@ForAll int anInt) {
			// Provider class with constant return but priority -1 should not be used
			assertThat(anInt).isNotEqualTo(414243);
		}
	}

	@Group
	class WithArbitraryConfiguratorClasses {

		@Property
		@Domain(ContextWithInnerConfiguratorClasses.class)
		@Domain(DomainContext.Global.class)
		void useConfiguratorFromInnerClass(@ForAll @Doubled String aString) {
			assertThat(aString.length()).isEven();
		}

		@Property
		@Domain(ContextIsArbitraryConfigurator.class)
		@Domain(DomainContext.Global.class)
		void useContextItselfAsConfigurator(@ForAll @Doubled String aString) {
			assertThat(aString.length()).isEven();
		}

		@Property
		@Domain(ContextWithInnerConfiguratorClasses.class)
		@Domain(DomainContext.Global.class)
		void useConfiguratorFromInnerStaticClass(@ForAll @MakeNegative int aNumber) {
			assertThat(aNumber).isNotPositive();
		}

	}

	@Group
	class WithDomainAnnotations {

		@Property
		@Domain(ContextWithDomainAnnotation.class)
		void directDomainAnnotation(@ForAll int anInt) {
			assertThat(anInt).isEqualTo(42);
		}

		@Property
		@Domain(ContextWithDomainAnnotation.class)
		void inheritedDomainAnnotation(@ForAll String aString) {
			assertThat(aString).isEqualTo("hello");
		}

	}

	@Group
	class WithReportingFormatClasses {

		@Property
		@Domain(ContextWithInnerReportingFormatClasses.class)
		void useReportingFormatFromInnerClass(@ForAll Instant instant) {
			assertThat(singleLineReport(instant)).isEqualTo("\"Instant()\"");
		}

		@Property
		@Domain(ContextWithInnerReportingFormatClasses.class)
		void useContextItselfAsReportingFormat(@ForAll Date aDate) {
			assertThat(singleLineReport(aDate)).isEqualTo("\"Date()\"");
		}

		@Property
		@Domain(ContextWithInnerReportingFormatClasses.class)
		void useReportingFormatFromStaticInnerClass(@ForAll LocalTime localTime) {
			assertThat(singleLineReport(localTime)).isEqualTo("\"LocalTime()\"");
		}

		@Property
		@Domain(ContextWithInnerReportingFormatClasses.class)
		void dontUsePrivateReportingFormat(@ForAll LocalDate localDate) {
			assertThat(singleLineReport(localDate)).isNotEqualTo("\"LocalDate()\"");
		}

	}
}

