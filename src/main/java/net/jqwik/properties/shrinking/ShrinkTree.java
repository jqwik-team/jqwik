package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkTree<T> implements Shrinkable<T> {

	public static <T> ShrinkTree<T> empty() {
		return new ShrinkTree<>();
	}

	private final List<List<Shrinkable<T>>> routes = new ArrayList<>();

	public void addRoute(List<Shrinkable<T>> route) {
		routes.add(route);
	}

	public List<List<Shrinkable<T>>> shrinkingRoutes() {
		return routes;
	}

	public Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier) {
		return routes.stream() //
			.map(route -> shrinkRoute(route, falsifier)) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.sorted(Comparator.naturalOrder()) //
			.findFirst();
	}

	private Optional<ShrinkResult<T>> shrinkRoute(List<Shrinkable<T>> route, Predicate<T> falsifier) {
		Optional<ShrinkResult<T>> lastFalsified = Optional.empty();
		for (Shrinkable<T> shrinkValue : route) {
			Optional<ShrinkResult<T>> shrinkResult = shrinkValue.shrink(falsifier);
			if (shrinkResult.isPresent()) {
				lastFalsified = shrinkResult;
			} else {
				break;
			}
		}
		return lastFalsified;
	}

}
