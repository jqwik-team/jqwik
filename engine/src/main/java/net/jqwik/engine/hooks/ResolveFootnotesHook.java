package net.jqwik.engine.hooks;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class ResolveFootnotesHook implements ResolveParameterHook, AroundTryHook {

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
				Store<List<String>> footnotesStore = optionalTry
					.map(this::getFootnotesStore)
					.orElse(getFootnotesStore(lifecycleContext));

				return (Footnotes) footnote -> {
					footnotesStore.get().add(footnote);
				};
			};
			return Optional.of(footnotesSupplier);
		}
		return Optional.empty();
	}

	private Store<List<String>> getFootnotesStore(LifecycleContext context) {
		if (context instanceof TryLifecycleContext) {
			return Store.getOrCreate(tryIdentifier(), Lifespan.TRY, ArrayList::new);
		} else if (context instanceof PropertyLifecycleContext) {
			return Store.getOrCreate(propertyIdentifier(), Lifespan.PROPERTY, ArrayList::new);
		} else {
			return Store.getOrCreate(containerIdentifier(), Lifespan.RUN, ArrayList::new);
		}
	}

	private Tuple.Tuple2<Class<ResolveFootnotesHook>, String> containerIdentifier() {
		return Tuple.of(ResolveFootnotesHook.class, "container");
	}

	private Tuple.Tuple2<Class<ResolveFootnotesHook>, String> propertyIdentifier() {
		return Tuple.of(ResolveFootnotesHook.class, "property");
	}

	private Tuple.Tuple2<Class<ResolveFootnotesHook>, String> tryIdentifier() {
		return Tuple.of(ResolveFootnotesHook.class, "try");
	}

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		TryExecutionResult executionResult = aTry.execute(parameters);
		if (executionResult.isFalsified()) {
			List<String> footnotes = getFootnotes(context);
			return executionResult.withFootnotes(footnotes);
		}
		return executionResult;
	}

	@Override
	public int aroundTryProximity() {
		return Hooks.AroundTry.TRY_RESOLVE_FOOTNOTES_PROXIMITY;
	}

	private List<String> getFootnotes(TryLifecycleContext context) {
		Store<List<String>> store = Store.get(tryIdentifier());
		return store.get();
	}
}
