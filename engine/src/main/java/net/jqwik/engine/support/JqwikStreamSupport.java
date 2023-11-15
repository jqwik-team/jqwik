package net.jqwik.engine.support;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class JqwikStreamSupport {

	/**
	 * From https://stackoverflow.com/a/46230233/32352
	 *
	 * @param leftStream  left
	 * @param rightStream right
	 * @param combiner    combine
	 * @param <L>         left type
	 * @param <R>         right type
	 * @param <T>         result type
	 * @return a zipped stream
	 */
	public static <L, R, T> Stream<T> zip(Stream<L> leftStream, Stream<R> rightStream, BiFunction<L, R, T> combiner) {
		Spliterator<L> lefts = leftStream.spliterator();
		Spliterator<R> rights = rightStream.spliterator();
		return StreamSupport.stream(
			new Spliterators.AbstractSpliterator<T>(
				Long.min(lefts.estimateSize(), rights.estimateSize()),
				lefts.characteristics() & rights.characteristics()
			) {
				@Override
				public boolean tryAdvance(Consumer<? super T> action) {
					return lefts.tryAdvance(left -> rights.tryAdvance(right -> {
						T combinedValue = combiner.apply(left, right);
						if (combinedValue == null) {
							return;
						}
						action.accept(combinedValue);
					}));
				}
			}, leftStream.isParallel() || rightStream.isParallel());
	}

	@SafeVarargs
	public static <T> Stream<T> concat(Stream<T>... streams) {
		// See discussion in https://github.com/jqwik-team/jqwik/issues/526 why StreamConcatenation is used
		return StreamConcatenation.concat(streams);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> concat(List<Stream<T>> streams) {
		return concat(streams.toArray(new Stream[0]));
	}

	/**
	 * Simulates Java 9's Stream.takeWhile()
	 * Taken from https://stackoverflow.com/a/46446546/32352
	 * <p>
	 * TODO: Remove when moving to Java &gt; 8
	 *
	 * @param stream a stream
	 * @param p      a predicate
	 * @param <T>    a type
	 * @return a stream
	 */
	public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<? super T> p) {
		class Taking extends Spliterators.AbstractSpliterator<T> implements Consumer<T> {
			private static final int CANCEL_CHECK_COUNT = 63;
			private final Spliterator<T> s;
			private int count;
			private T t;
			private final AtomicBoolean cancel = new AtomicBoolean();
			private boolean takeOrDrop = true;

			Taking(Spliterator<T> s) {
				super(s.estimateSize(), s.characteristics() & ~(Spliterator.SIZED | Spliterator.SUBSIZED));
				this.s = s;
			}

			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				boolean test = true;
				if (takeOrDrop &&               // If can take
						(count != 0 || !cancel.get()) && // and if not cancelled
						s.tryAdvance(this) &&   // and if advanced one element
						(test = p.test(t))) {   // and test on element passes
					action.accept(t);           // then accept element
					return true;
				} else {
					// Taking is finished
					takeOrDrop = false;
					// Cancel all further traversal and splitting operations
					// only if test of element failed (short-circuited)
					if (!test)
						cancel.set(true);
					return false;
				}
			}

			@Override
			public Comparator<? super T> getComparator() {
				return s.getComparator();
			}

			@Override
			public void accept(T t) {
				count = (count + 1) & CANCEL_CHECK_COUNT;
				this.t = t;
			}

			@Override
			public Spliterator<T> trySplit() {
				return null;
			}
		}
		return StreamSupport.stream(new Taking(stream.spliterator()), stream.isParallel()).onClose(stream::close);
	}

	public static <T> Stream<T> toStream(Optional<T> optional) {
		return optional.map(Stream::of).orElse(Stream.empty());
	}
}
