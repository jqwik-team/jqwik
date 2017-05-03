package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public interface Falsifiable<T> {

	Optional<ShrinkResult<T>> falsify(Predicate<T> falsifier);
}
