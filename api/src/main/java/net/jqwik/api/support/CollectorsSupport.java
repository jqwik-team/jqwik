package net.jqwik.api.support;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Provide implementation for stream to collect to LinkedHashSet in order to preserve order
 * and to make random-based generation deterministic
 */
@API(status = INTERNAL)
public class CollectorsSupport {
	public static <T> Collector<T, ?, Set<T>> toLinkedHashSet() {
		return Collectors.toCollection(LinkedHashSet::new);
	}
}
