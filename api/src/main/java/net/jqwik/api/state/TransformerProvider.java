package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface TransformerProvider<T> extends Function<Supplier<T>, Arbitrary<Transformer<T>>> {
}
