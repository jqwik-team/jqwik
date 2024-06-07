package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An ActionBuilder is used to create simple {@linkplain Action} objects.
 * Actions are simple if their state transformation does not depend on the state itself.
 * However, even simple actions can have preconditions.
 *
 * <p>
 * Alternatively you can subclass {@linkplain Action.JustTransform} or {@linkplain Action.JustMutate}.
 * </p>
 *
 * <p>
 * For actions whose state transformation depends on the state,
 * you have to provide implementations of either {@linkplain Action.Dependent}
 * or {@linkplain Action.Independent}.
 * </p>
 *
 * @param <S> Type of the object to transform through an action
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public class ActionBuilder<S extends @Nullable Object> {

	final private @Nullable Predicate<? super S> precondition;

	final private @Nullable String description;

	ActionBuilder() {
		this(null, null);
	}

	private ActionBuilder(@Nullable Predicate<? super S> precondition, @Nullable String description) {
		this.precondition = precondition;
		this.description = description;
	}

	public ActionBuilder<S> when(Predicate<? super S> precondition) {
		return new ActionBuilder<>(precondition, description);
	}

	public ActionBuilder<S> describeAs(String description) {
		return new ActionBuilder<>(precondition, description);
	}

	public Action.Independent<S> just(Transformer<S> transformer) {
		if (precondition == null) {
			return () -> justTransformer(transformer);
		}
		return new Action.Independent<S>() {
			@Override
			public Arbitrary<Transformer<S>> transformer() {
				return justTransformer(transformer);
			}

			@Override
			public boolean precondition(S state) {
				return precondition.test(state);
			}
		};
	}

	private Arbitrary<Transformer<S>> justTransformer(Transformer<S> transformer) {
		Transformer<S> withDescription = description == null ? transformer : Transformer.transform(description, transformer);
		return Arbitraries.just(withDescription);
	}

	public Action.Independent<S> justMutate(Consumer<S> mutatingFunction) {
		Transformer<S> transformer = state -> {
			mutatingFunction.accept(state);
			return state;
		};
		return just(transformer);
	}
}
