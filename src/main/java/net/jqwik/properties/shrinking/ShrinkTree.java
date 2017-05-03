package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkTree<T> implements Falsifiable<T> {

	public static <T> ShrinkTree<T> empty() {
		return new ShrinkTree<>();
	}

	private final List<List<Falsifiable<T>>> routes = new ArrayList<>();

	public void addRoute(List<Falsifiable<T>> route) {
		routes.add(route);
	}

	public List<List<Falsifiable<T>>> shrinkingRoutes() {
		return routes;
	}

	public Optional<ShrinkResult<T>> falsify(Predicate<T> falsifier) {
		return routes.stream() //
			.map(route -> shrinkRoute(route, falsifier)) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.sorted(Comparator.naturalOrder()) //
			.findFirst();
	}

	private Optional<ShrinkResult<T>> shrinkRoute(List<Falsifiable<T>> route, Predicate<T> falsifier) {
		Optional<ShrinkResult<T>> lastFalsified = Optional.empty();
		for (Falsifiable<T> shrinkValue : route) {
			Optional<ShrinkResult<T>> shrinkResult = shrinkValue.falsify(falsifier);
			if (shrinkResult.isPresent()) {
				lastFalsified = shrinkResult;
			} else {
				break;
			}
		}
		return lastFalsified;
	}

}
