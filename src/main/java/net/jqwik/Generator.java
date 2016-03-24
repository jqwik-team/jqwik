package net.jqwik;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Generator<T> {

    T generate();

    default Stream<T> generateAll() {
        return new ArrayList<T>().stream();
    }

    List<T> shrink(T value);

    default Optional<Long> finalNumberOfValues() {
        return Optional.empty();
    }
}
