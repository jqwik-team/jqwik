package net.jqwik.engine.support;

import java.util.*;
import java.util.stream.*;

public class JqwikCollectors {
	public static <T> Collector<T, ?, Set<T>> toLinkedHashSet() {
		return Collectors.toCollection(LinkedHashSet::new);
	}
}
