package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public interface NShrinkable<T> {

	Set<NShrinkable<T>> shrink();

	boolean falsifies(Predicate<T> falsifier);

	T value();

	int distance();
}
