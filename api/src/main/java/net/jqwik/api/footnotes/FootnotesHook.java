package net.jqwik.api.footnotes;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

@API(status = API.Status.INTERNAL)
class FootnotesHook implements RegistrarHook {

	private static Store<FootnotesCollector> getFootnotesCollectorStore() {
		return Store.getOrCreate(
			Tuple.of(FootnotesHook.class, "footnotes"),
			Lifespan.TRY, FootnotesCollector::new
		);
	}

	@Override
	public void registerHooks(Registrar registrar) {
		registrar.register(FootnotesResolveParameter.class, PropagationMode.ALL_DESCENDANTS);
		registrar.register(FootnotesInnermost.class, PropagationMode.ALL_DESCENDANTS);
		registrar.register(FootnotesOutermost.class, PropagationMode.ALL_DESCENDANTS);
	}

	static class FootnotesResolveParameter implements ResolveParameterHook {

		@Override
		public Optional<ParameterSupplier> resolve(
			ParameterResolutionContext parameterContext,
			LifecycleContext lifecycleContext
		) {
			if (parameterContext.typeUsage().isOfType(Footnotes.class)) {
				ParameterSupplier footnotesSupplier = optionalTry -> {
					Tuple2<String, Store<FootnotesCollector>> labelAndStore =
						optionalTry
							.map(tryLifecycleContext -> Tuple.of(tryLifecycleContext.label(), getFootnotesCollectorStore()))
							.orElseThrow(() -> {
								String message = String.format(
									"Illegal argument [%s] in method [%s].%n" +
										"Objects of type %s can only be injected directly " +
										"in property methods or in @BeforeTry and @AfterTry " +
										"lifecycle methods.",
									parameterContext.parameter(),
									parameterContext.optionalMethod()
													.map(Method::toString)
													.orElse("unknown"),
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
	}

	static class FootnotesOutermost implements AroundTryHook {

		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
			TryExecutionResult executionResult = aTry.execute(parameters);
			if (executionResult.isFalsified()) {
				getFootnotesCollectorStore().get().evaluateOutermost();
				return executionResult.withFootnotes(getFootnotes());
			}
			return executionResult;
		}

		private List<String> getFootnotes() {
			return getFootnotesCollectorStore().get().getFootnotes();
		}

		@Override
		public int aroundTryProximity() {
			// Outside lifecycle methods
			return -20;
		}
	}

	static class FootnotesInnermost implements AroundTryHook {

		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
			TryExecutionResult executionResult = aTry.execute(parameters);
			if (executionResult.isFalsified()) {
				getFootnotesCollectorStore().get().evaluateInnermost();
			}
			return executionResult;
		}

		@Override
		public int aroundTryProximity() {
			// Mostly first thing after property method execution
			return 99;
		}
	}

	private static class FootnotesCollector {

		private final List<String> footnotes = new ArrayList<>();
		private final List<String> collectedFootnotes = new ArrayList<>();
		private final List<Supplier<String>> collectedSuppliers = new ArrayList<>();

		private List<String> getFootnotes() {
			return footnotes;
		}

		private void addFootnote(String footnote) {
			collectedFootnotes.add(footnote);
		}

		private void addAfterFailure(Supplier<String> footnoteSupplier) {
			collectedSuppliers.add(footnoteSupplier);
		}

		private void evaluateInnermost() {
			for (Supplier<String> footnoteSupplier : collectedSuppliers) {
				footnotes.add(footnoteSupplier.get());
			}
		}

		private void evaluateOutermost() {
			for (String footnote : collectedFootnotes) {
				footnotes.add(footnote);
			}
		}
	}

	private static class StoreBasedFootnotes implements Footnotes {

		private final String label;
		private final Store<FootnotesCollector> store;

		private StoreBasedFootnotes(Tuple2<String, Store<FootnotesCollector>> labelAndStore) {
			this.label = labelAndStore.get1();
			this.store = labelAndStore.get2();
		}

		@Override
		public void addFootnote(String footnote) {
			store.get().addFootnote(footnote);
		}

		@Override
		public void addAfterFailure(Supplier<String> footnoteSupplier) {
			store.get().addAfterFailure(footnoteSupplier);
		}

		@Override
		public String toString() {
			return String.format("%s[%s]", Footnotes.class.getName(), label);
		}
	}
}
