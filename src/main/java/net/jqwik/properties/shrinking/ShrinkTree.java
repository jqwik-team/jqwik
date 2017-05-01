package net.jqwik.properties.shrinking;

import java.util.*;

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
}
