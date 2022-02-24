package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Step<T> extends Function<T, T> {
}
