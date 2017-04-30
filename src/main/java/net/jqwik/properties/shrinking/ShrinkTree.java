package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.stream.*;

public class ShrinkTree<T> implements Iterable<ShrinkValue<T>> {

	public static <T> ShrinkTree<T> empty() {
		return new ShrinkTree<>();
	}

	private final List<ShrinkValue<T>> nodes = new ArrayList<>();

	@Override
	public Iterator<ShrinkValue<T>> iterator() {
		return nodes.iterator();
	}

	public Stream<ShrinkValue<T>> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public void add(ShrinkValue<T> shrinkNode) {
		nodes.add(shrinkNode);
	}

}
