package net.jqwik.engine.support;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class JqwikStreamSupport {

	/**
	 * From https://stackoverflow.com/a/46230233/32352
	 */
	public static <L, R, T> Stream<T> zip(Stream<L> leftStream, Stream<R> rightStream, BiFunction<L, R, T> combiner) {
		Spliterator<L> lefts = leftStream.spliterator();
		Spliterator<R> rights = rightStream.spliterator();
		return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.min(lefts.estimateSize(), rights.estimateSize()), lefts
																																	   .characteristics() & rights
																																								.characteristics()) {
			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				return lefts.tryAdvance(left -> rights.tryAdvance(right -> action.accept(combiner.apply(left, right))));
			}
		}, leftStream.isParallel() || rightStream.isParallel());
	}

	@SafeVarargs
	public static <T> Stream<T> concat(Stream<T>... streams) {
		return concat(Arrays.asList(streams));
	}

	public static <T> Stream<T> concat(List<Stream<T>> streams) {
		return concat(Stream.empty(), new ArrayList<>(streams));
	}

	private static <T> Stream<T> concat(Stream<T> head, List<Stream<T>> rest) {
		if (rest.isEmpty()) {
			return head;
		}
		Stream<T> first = rest.remove(0);
		return Stream.concat(head, concat(first, rest));
	}
}
