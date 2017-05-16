package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinker<T> {

	Set<NShrinkable<T>> shrink();
}
