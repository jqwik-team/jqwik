package net.jqwik.api.support;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Provide implementation for stream to collect to LinkedHashSet in order to preserve order
 * and to make random-based generation deterministic
 */
@API(status = INTERNAL)
public class CollectorsSupport {

	private CollectorsSupport() {}

	public static <T extends @Nullable Object> Collector<T, ?, Set<T>> toLinkedHashSet() {
		return Collectors.toCollection(LinkedHashSet::new);
	}
}
