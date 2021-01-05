package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

class ResolvingParametersInTryTests {

	@Property(tries = 10)
	@AddLifecycleHook(CreateAString.class)
	void resolveLastPosition(@ForAll @IntRange(min = 3, max = 3) int anInt, String aString) {
		assertThat(anInt).isEqualTo(3);
		assertThat(aString).isEqualTo("aString");
	}

	@Property(tries = 10)
	@AddLifecycleHook(CreateAString.class)
	void resolveFirstPosition(String aString, @ForAll @IntRange(min = 3, max = 3) int anInt) {
		assertThat(anInt).isEqualTo(3);
		assertThat(aString).isEqualTo("aString");
	}

	@Property(tries = 10)
	@AddLifecycleHook(CreateAString.class)
	@AddLifecycleHook(Create42.class)
	@PerProperty(Assert20Supplier2InjectorCalls.class)
	void resolveSeveralPositions(String aString1, int fourtytwo1, String aString2, int fourtytwo2) {
		assertThat(fourtytwo1).isEqualTo(42);
		assertThat(fourtytwo2).isEqualTo(42);
		assertThat(aString1).isEqualTo("aString");
		assertThat(aString2).isEqualTo("aString");
	}

	class Assert20Supplier2InjectorCalls implements Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(CreateAString.countSupplierCalls.get()).isEqualTo(20);
			assertThat(CreateAString.countResolveCalls.get()).isEqualTo(2);
		}
	}

	@Property(tries = 10)
	@AddLifecycleHook(CreateAString.class)
	@PerProperty(Assert1ResolveCalls.class)
	void resolverIsCalledOnce(@ForAll int ignore, String aString) {
		assertThat(aString).isEqualTo("aString");
	}

	class Assert1ResolveCalls implements Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(CreateAString.countResolveCalls.get()).isEqualTo(1);
		}
	}

	@Property(tries = 10)
	@AddLifecycleHook(CreateAString.class)
	@PerProperty(Assert10SupplierCalls.class)
	void supplierIsCalledOncePerTry(@ForAll int ignore, String aString) {
	}

	class Assert10SupplierCalls implements Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(CreateAString.countSupplierCalls.get()).isEqualTo(10);
		}
	}

	@Property(tries = 10, afterFailure = AfterFailureMode.RANDOM_SEED)
	@AddLifecycleHook(CreateAString.class)
	@ExpectFailure(checkResult = ShrinkTo7.class)
	void shouldShrinkTo7(@ForAll @IntRange(min = 0) int anInt, String aString) {
		assertThat(aString).isEqualTo("aString");
		assertThat(anInt).isLessThan(7);
	}

	private class ShrinkTo7 extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return Arrays.asList(7, "aString");
		}
	}

	@Example
	@AddLifecycleHook(CreateAString.class)
	@ExpectFailure(failureType = CannotResolveParameterException.class)
	void shouldFailWithCannotResolveException(@ForAll int generated, int notResolved) {
	}

	@Example
	@AddLifecycleHook(CreateAlwaysAString.class)
	@ExpectFailure(failureType = CannotResolveParameterException.class)
	void shouldFailWithWrongParameterValueType(List<Integer> aList) {
	}

	@Example
	@AddLifecycleHook(CreateAString.class)
	@AddLifecycleHook(CreateAlwaysAString.class)
	@ExpectFailure(failureType = CannotResolveParameterException.class)
	void shouldFailWithDuplicateResolution(String aString) {
	}

	@Property(tries = 5)
	@PerProperty(Insert42Lifecycle.class)
	void check42(int shouldBe42) {
		assertThat(shouldBe42).isEqualTo(42);
	}

	private class Insert42Lifecycle implements Lifecycle {

		Store<LifecycleContext> lifecycleStore = Store.create(this, Lifespan.PROPERTY, () -> null);

		@Override
		public Optional<ResolveParameterHook.ParameterSupplier> resolve(ParameterResolutionContext parameterContext) {
			return Optional.of(optionalTry -> {
				assertThat(optionalTry).isPresent();
				assertThat(lifecycleStore.get()).isNotSameAs(optionalTry.get());
				lifecycleStore.update(ignore -> optionalTry.get());
				return 42;
			});
		}
	}

}

class CreateAlwaysAString implements ResolveParameterHook {
	@Override
	public Optional<ParameterSupplier> resolve(
		ParameterResolutionContext parameterContext,
		LifecycleContext lifecycleContext
	) {
		return Optional.of(ignore -> "a string");
	}
}

class CreateAString implements ResolveParameterHook {

	static Store<Integer> countResolveCalls;

	static Store<Integer> countSupplierCalls;

	@Override
	public Optional<ParameterSupplier> resolve(
		ParameterResolutionContext parameterContext,
		LifecycleContext context
	) {
		countResolveCalls = Store.getOrCreate("injectorCalls", Lifespan.PROPERTY, () -> 0);
		countSupplierCalls = Store.getOrCreate("supplierCalls", Lifespan.PROPERTY, () -> 0);

		assertThat(context).isInstanceOf(PropertyLifecycleContext.class);
		assertThat(context.optionalContainerClass().get()).isEqualTo(ResolvingParametersInTryTests.class);

		if (parameterContext.typeUsage().isOfType(String.class)) {
			countResolveCalls.update(i -> i + 1);
			return Optional.of(optionalTry -> {
				assertThat(optionalTry).isPresent();
				countSupplierCalls.update(i -> i + 1);
				return "aString";
			});
		}
		return Optional.empty();
	}
}

class Create42 implements ResolveParameterHook {

	@Override
	public Optional<ParameterSupplier> resolve(
		ParameterResolutionContext parameterContext,
		LifecycleContext lifecycleContext
	) {
		assertThat(parameterContext.index()).isBetween(0, 3);
		if (parameterContext.typeUsage().isOfType(int.class)) {
			return Optional.of(ignore -> 42);
		}
		return Optional.empty();
	}
}