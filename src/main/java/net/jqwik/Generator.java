package net.jqwik;

import java.util.List;

public interface Generator<T> {

    T generate();

    List<T> shrink(T value);

    boolean canServeType(Class<?> type);
}
