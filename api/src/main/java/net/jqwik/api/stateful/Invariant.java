package net.jqwik.api.stateful;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = MAINTAINED, since = "1.0")
public interface Invariant<T extends @Nullable Object> {

	void check(T model);
}
