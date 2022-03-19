package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Provider a state {@linkplain Transformer transformer} for values of type {@code T} in the context of {@linkplain Chain chains}.
 * Returning {@code null} signals that this provider is not applicable for the previous state,
 * which can be retrieved using the first {@linkplain Supplier supplier} argument of the function.
 *
 * @param <T> The type of state to be transformed in a chain
 *
 * @see Chain
 * @see Transformer
 */
@FunctionalInterface
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface TransformerProvider<T> extends Function<Supplier<T>, @Nullable Arbitrary<Transformer<T>>> {
}
