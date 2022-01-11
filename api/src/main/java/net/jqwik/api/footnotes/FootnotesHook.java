package net.jqwik.api.footnotes;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

@API(status = API.Status.INTERNAL)
class FootnotesHook implements ResolveParameterHook, AroundTryHook {

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public Optional<ParameterSupplier> resolve(
		ParameterResolutionContext parameterContext,
		LifecycleContext lifecycleContext
	) {
		if (parameterContext.typeUsage().isOfType(Footnotes.class)) {
			ParameterSupplier footnotesSupplier = optionalTry -> {
				Tuple2<String, Store<List<String>>> labelAndStore = optionalTry
					.map(tryLifecycleContext -> Tuple.of(tryLifecycleContext.label(), getFootnotesStore()))
					.orElseThrow(() -> {
						String message = String.format(
							"Illegal argument [%s] in method [%s].%n" +
								"Objects of type %s can only be injected directly " +
								"in property methods or in @BeforeTry and @AfterTry " +
								"lifecycle methods.",
							parameterContext.parameter(),
							parameterContext.optionalMethod().map(Method::toString).orElse("unknown"),
							Footnotes.class
						);
						return new IllegalArgumentException(message);
					});

				return new StoreBasedFootnotes(labelAndStore);
			};
			return Optional.of(footnotesSupplier);
		}
		return Optional.empty();
	}

	private Store<List<String>> getFootnotesStore() {
		return Store.getOrCreate(
			Tuple.of(FootnotesHook.class, "footnotes"),
			Lifespan.TRY, initializer -> initializer.initialValue(new ArrayList<>())
		);
	}

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		TryExecutionResult executionResult = aTry.execute(parameters);
		if (executionResult.isFalsified()) {
			List<String> footnotes = getFootnotes();
			return executionResult.withFootnotes(footnotes);
		}
		return executionResult;
	}

	@Override
	public int aroundTryProximity() {
		// Outside lifecycle methods
		return -20;
	}

	private List<String> getFootnotes() {
		return getFootnotesStore().get();
	}

	private static class StoreBasedFootnotes implements Footnotes {

		private final String label;
		private final Store<List<String>> store;

		public StoreBasedFootnotes(Tuple2<String, Store<List<String>>> labelAndStore) {
			this.label = labelAndStore.get1();
			this.store = labelAndStore.get2();
		}

		@Override
		public void addFootnote(String footnote) {
			store.get().add(footnote);
		}

		@Override
		public String toString() {
			return String.format("%s[%s]", Footnotes.class.getName(), label);
		}
	}
}
