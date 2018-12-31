package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = MAINTAINED, since = "1.0")
public interface Invariant<T> {

	void check(T model);
}
