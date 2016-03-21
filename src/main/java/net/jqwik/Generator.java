package net.jqwik;

public interface Generator<T> {

    T generate();

    T shrink(T value);

    boolean canServeType(Class<?> type);
}
