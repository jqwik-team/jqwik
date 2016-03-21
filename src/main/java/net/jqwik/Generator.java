package net.jqwik;

public interface Generator<T> {

    T generate();

    boolean canServeType(Class<?> type);
}
