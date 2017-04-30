package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.stream.*;

public class ShrinkTree<T> implements Iterable<ShrinkValue<T>> {

	@Override
	public Iterator<ShrinkValue<T>> iterator() {
		return null;
	}

	public Stream<ShrinkValue<T>> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}
}
