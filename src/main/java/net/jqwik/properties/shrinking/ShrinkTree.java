package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ShrinkTree<T> {

	public static <T> ShrinkTree<T> empty() {
		return new ShrinkTree<>();
	}

	private final List<List<ShrinkValue<T>>> routes = new ArrayList<>();

	public void addRoute(List<ShrinkValue<T>> route) {
		routes.add(route);
	}

	public List<List<ShrinkValue<T>>> shrinkingRoutes() {
		return routes;
	}

	public List<ShrinkResult<T>> falsify(Predicate<T> falsifier) {
		return routes.stream() //
			.map(route -> shrinkRoute(route, falsifier)) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.collect(Collectors.toList());
	}

	public Optional<ShrinkResult<T>> shrinkRoute(List<ShrinkValue<T>> route, Predicate<T> falsifier) {
		ShrinkResult<T> lastFalsified = null;
		for (ShrinkValue<T> shrinkValue : route) {
			ShrinkResult<T> shrinkResult = falsify(shrinkValue, falsifier);
			if (shrinkResult != null) {
				lastFalsified = shrinkResult;
			} else {
				break;
			}
		}
		return Optional.ofNullable(lastFalsified);
	}

	private ShrinkResult<T> falsify(ShrinkValue<T> shrinkValue, Predicate<T> falsifier) {
		try {
			if (falsifier.test(shrinkValue.value()))
				return null;
			return ShrinkResult.of(shrinkValue, null);
		} catch (AssertionError assertionError) {
			return ShrinkResult.of(shrinkValue, assertionError);
		} catch (Throwable any) {
			return null;
		}
	}
}
