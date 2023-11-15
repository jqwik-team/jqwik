package net.jqwik.engine.support;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

// @formatter:off
/**
 * <p>
 * Stolen from https://github.com/TechEmpower/misc/blob/master/concat/src/main/java/rnd/StreamConcatenation.java.
 * See discussion in https://github.com/jqwik-team/jqwik/issues/526 why StreamConcatenation is used.
 * </p>
 *
 * Utility methods for concatenating streams.
 *
 * @author Michael Hixson
 */
final class StreamConcatenation {
	private StreamConcatenation() {
		throw new AssertionError("This class cannot be instantiated");
	}

	/**
	 * Creates a lazily concatenated stream whose elements are the elements of
	 * each of the input streams.  In other words, the returned stream contains
	 * all the elements of the first input stream followed by all the elements of
	 * the second input stream, and so on.
	 *
	 * <p>Although this method does not eagerly consume the elements of the input
	 * streams, this method is a terminal operation for the input streams.
	 *
	 * <p>The returned stream is parallel if any of the input streams is parallel.
	 *
	 * <p>When the returned stream is closed, the close handlers for all the input
	 * streams are invoked.  If one of those handlers throws an exception, that
	 * exception will be rethrown after the remaining handlers are invoked.  If
	 * the remaining handlers throw exceptions, those exceptions are added as
	 * suppressed exceptions of the first.
	 *
	 * <p>If the argument array or any of the input streams are modified after
	 * being passed to this method, the behavior of this method is undefined.
	 *
	 * @param <T>     the type of stream elements
	 * @param streams the streams to be concatenated
	 * @return the concatenation of the input streams
	 * @throws NullPointerException if the argument array or any of the input
	 *                              streams are {@code null}
	 */
	@SafeVarargs
	@SuppressWarnings({"unchecked", "varargs"}) // TODO: Explain why this is ok.
	public static <T> Stream<T> concat(Stream<? extends T>... streams) {
		return concatInternal(
			(Stream<T>[]) streams,
			size -> (Spliterator<T>[]) new Spliterator<?>[size],
			Stream::spliterator,
			ConcatSpliterator.OfRef::new,
			StreamSupport::stream
		);
	}

	/**
	 * Creates a lazily concatenated stream whose elements are the elements of
	 * each of the input streams.  In other words, the returned stream contains
	 * all the elements of the first input stream followed by all the elements of
	 * the second input stream, and so on.
	 *
	 * <p>Although this method does not eagerly consume the elements of the input
	 * streams, this method is a terminal operation for the input streams.
	 *
	 * <p>The returned stream is parallel if any of the input streams is parallel.
	 *
	 * <p>When the returned stream is closed, the close handlers for all the input
	 * streams are invoked.  If one of those handlers throws an exception, that
	 * exception will be rethrown after the remaining handlers are invoked.  If
	 * the remaining handlers throw exceptions, those exceptions are added as
	 * suppressed exceptions of the first.
	 *
	 * <p>If the argument array or any of the input streams are modified after
	 * being passed to this method, the behavior of this method is undefined.
	 *
	 * @param streams the streams to be concatenated
	 * @return the concatenation of the input streams
	 * @throws NullPointerException if the argument array or any of the input
	 *                              streams are {@code null}
	 */
	public static IntStream concat(IntStream... streams) {
		return concatInternal(
			streams,
			Spliterator.OfInt[]::new,
			IntStream::spliterator,
			ConcatSpliterator.OfInt::new,
			StreamSupport::intStream
		);
	}

	/**
	 * Creates a lazily concatenated stream whose elements are the elements of
	 * each of the input streams.  In other words, the returned stream contains
	 * all the elements of the first input stream followed by all the elements of
	 * the second input stream, and so on.
	 *
	 * <p>Although this method does not eagerly consume the elements of the input
	 * streams, this method is a terminal operation for the input streams.
	 *
	 * <p>The returned stream is parallel if any of the input streams is parallel.
	 *
	 * <p>When the returned stream is closed, the close handlers for all the input
	 * streams are invoked.  If one of those handlers throws an exception, that
	 * exception will be rethrown after the remaining handlers are invoked.  If
	 * the remaining handlers throw exceptions, those exceptions are added as
	 * suppressed exceptions of the first.
	 *
	 * <p>If the argument array or any of the input streams are modified after
	 * being passed to this method, the behavior of this method is undefined.
	 *
	 * @param streams the streams to be concatenated
	 * @return the concatenation of the input streams
	 * @throws NullPointerException if the argument array or any of the input
	 *                              streams are {@code null}
	 */
	public static LongStream concat(LongStream... streams) {
		return concatInternal(
			streams,
			Spliterator.OfLong[]::new,
			LongStream::spliterator,
			ConcatSpliterator.OfLong::new,
			StreamSupport::longStream
		);
	}

	/**
	 * Creates a lazily concatenated stream whose elements are the elements of
	 * each of the input streams.  In other words, the returned stream contains
	 * all the elements of the first input stream followed by all the elements of
	 * the second input stream, and so on.
	 *
	 * <p>Although this method does not eagerly consume the elements of the input
	 * streams, this method is a terminal operation for the input streams.
	 *
	 * <p>The returned stream is parallel if any of the input streams is parallel.
	 *
	 * <p>When the returned stream is closed, the close handlers for all the input
	 * streams are invoked.  If one of those handlers throws an exception, that
	 * exception will be rethrown after the remaining handlers are invoked.  If
	 * the remaining handlers throw exceptions, those exceptions are added as
	 * suppressed exceptions of the first.
	 *
	 * <p>If the argument array or any of the input streams are modified after
	 * being passed to this method, the behavior of this method is undefined.
	 *
	 * @param streams the streams to be concatenated
	 * @return the concatenation of the input streams
	 * @throws NullPointerException if the argument array or any of the input
	 *                              streams are {@code null}
	 */
	public static DoubleStream concat(DoubleStream... streams) {
		return concatInternal(
			streams,
			Spliterator.OfDouble[]::new,
			DoubleStream::spliterator,
			ConcatSpliterator.OfDouble::new,
			StreamSupport::doubleStream
		);
	}

	// The generics and function objects are ugly, but this method lets us reuse
	// the same logic in all the public concat(...) methods.
	private static <
					   T,
					   T_SPLITR extends Spliterator<T>,
					   T_STREAM extends BaseStream<T, T_STREAM>>
	T_STREAM concatInternal(
		T_STREAM[] streams,
		IntFunction<T_SPLITR[]> arrayFunction,
		Function<T_STREAM, T_SPLITR> spliteratorFunction,
		Function<T_SPLITR[], T_SPLITR> concatFunction,
		BiFunction<T_SPLITR, Boolean, T_STREAM> streamFunction
	) {
		T_SPLITR[] spliterators = arrayFunction.apply(streams.length);
		boolean parallel = false;
		for (int i = 0; i < streams.length; i++) {
			T_STREAM inStream = streams[i];
			T_SPLITR inSpliterator = spliteratorFunction.apply(inStream);
			spliterators[i] = inSpliterator;
			parallel = parallel || inStream.isParallel();
		}
		T_SPLITR outSpliterator = concatFunction.apply(spliterators);
		T_STREAM outStream = streamFunction.apply(outSpliterator, parallel);
		return outStream.onClose(new ComposedClose(streams));
	}

	abstract static class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>>
		implements Spliterator<T> {

		final T_SPLITR[] spliterators;
		int low; // increases only after trySplit()
		int cursor; // increases after iteration or trySplit()
		final int high;

		ConcatSpliterator(T_SPLITR[] spliterators, int fromIndex, int toIndex) {
			this.spliterators = spliterators;
			low = cursor = fromIndex;
			high = toIndex;
		}

		// Having these two abstract methods let us reuse the same trySplit()
		// implementation in all subclasses.

		// invokes spliterator.trySplit() on the argument
		abstract T_SPLITR invokeTrySplit(T_SPLITR spliterator);

		// invokes our constructor with the same array but modified bounds
		abstract T_SPLITR slice(int fromIndex, int toIndex);

		@Override
		public int characteristics() {
			int i = low; // ignore the cursor; iteration can't affect characteristics
			if (i >= high) {
				// TODO(perf): This may report fewer characteristics than it should.
				return Spliterators.emptySpliterator().characteristics();
			}
			if (i == high - 1) {
				// note for getComparator(): this is the only time we might be SORTED
				return spliterators[i].characteristics();
			}
			//
			// DISTINCT and SORTED are *not* safe to inherit.  Imagine our input
			// spliterators each contain the elements [1, 2] (distinct and sorted) and
			// so we are [1, 2, 1, 2] (neither distinct nor sorted).
			//
			// SIZED and SUBSIZED might be safe to inherit, but we have to account for
			// arithmetic overflow.  If we're unable to produce a correct sum in
			// estimateSize() due to overflow, we must not report SIZED (or SUBSIZED).
			//
			// ORDERED, NONNULL, IMMUTABLE, and CONCURRENT are safe to inherit.
			//
			// We assume that all other characteristics not listed here (that do not
			// exist yet at the time of this writing) are *not* safe to inherit.
			// False negatives generally result in degraded performance, while false
			// positives generally result in incorrect behavior.
			//
			int characteristics = Spliterator.ORDERED
									  | Spliterator.SIZED
									  | Spliterator.SUBSIZED
									  | Spliterator.NONNULL
									  | Spliterator.IMMUTABLE
									  | Spliterator.CONCURRENT;
			long size = 0;
			do {
				Spliterator<T> spliterator = spliterators[i];
				characteristics &= spliterator.characteristics();
				if ((characteristics & Spliterator.SIZED) == Spliterator.SIZED) {
					size += spliterator.estimateSize();
					if (size < 0) { // overflow
						characteristics &= ~(Spliterator.SIZED | Spliterator.SUBSIZED);
					}
				}
			} while (++i < high);
			return characteristics;
		}

		@Override
		public long estimateSize() {
			long size = 0;
			for (int i = cursor; i < high; i++) {
				size += spliterators[i].estimateSize();
				if (size < 0) { // overflow
					return Long.MAX_VALUE;
				}
			}
			return size;
		}

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			Objects.requireNonNull(action);
			int i = cursor;
			if (i < high) {
				do {
					spliterators[i].forEachRemaining(action);
				} while (++i < high);
				cursor = high;
			}
		}

		@Override
		public Comparator<? super T> getComparator() {
			int i = low; // like characteristics()
			if (i == high - 1) {
				// this is the only time we might be SORTED; see characteristics()
				return spliterators[i].getComparator();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			Objects.requireNonNull(action);
			int i = cursor;
			if (i < high) {
				do {
					if (spliterators[i].tryAdvance(action)) {
						cursor = i;
						return true;
					}
				} while (++i < high);
				cursor = high;
			}
			return false;
		}

		@Override
		public T_SPLITR trySplit() {
			//
			// TODO(perf): Should we split differently when we're SIZED?
			//
			// 1) Rather than splitting our *spliterators* in half, we could try to
			//    split our *elements* in half.
			//
			// 2) We could refuse to split if our total element count is lower than
			//    some threshold.
			//
			int i = cursor;
			if (i >= high) {
				return null;
			}
			if (i == high - 1) {
				return invokeTrySplit(spliterators[i]);
			}
			int mid = (i + high) >>> 1;
			low = cursor = mid;
			if (mid == i + 1) {
				return spliterators[i];
			}
			return slice(i, mid);
		}

		static final class OfRef<T>
			extends ConcatSpliterator<T, Spliterator<T>> {

			OfRef(Spliterator<T>[] spliterators) {
				super(spliterators, 0, spliterators.length);
			}

			OfRef(Spliterator<T>[] spliterators, int fromIndex, int toIndex) {
				super(spliterators, fromIndex, toIndex);
			}

			@Override
			Spliterator<T> invokeTrySplit(Spliterator<T> spliterator) {
				return spliterator.trySplit();
			}

			@Override
			Spliterator<T> slice(int fromIndex, int toIndex) {
				return new ConcatSpliterator.OfRef<>(spliterators, fromIndex, toIndex);
			}
		}

		abstract static class OfPrimitive<
											 T,
											 T_CONS,
											 T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>>
			extends ConcatSpliterator<T, T_SPLITR>
			implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {

			OfPrimitive(T_SPLITR[] spliterators, int fromIndex, int toIndex) {
				super(spliterators, fromIndex, toIndex);
			}

			// TODO: Is there a good way to share this logic with the base class?

			@Override
			public void forEachRemaining(T_CONS action) {
				Objects.requireNonNull(action);
				int i = cursor;
				if (i < high) {
					do {
						spliterators[i].forEachRemaining(action);
					} while (++i < high);
					cursor = high;
				}
			}

			@Override
			public boolean tryAdvance(T_CONS action) {
				Objects.requireNonNull(action);
				int i = cursor;
				if (i < high) {
					do {
						if (spliterators[i].tryAdvance(action)) {
							cursor = i;
							return true;
						}
					} while (++i < high);
					cursor = high;
				}
				return false;
			}
		}

		static final class OfInt
			extends ConcatSpliterator.OfPrimitive<
													 Integer,
													 IntConsumer,
													 Spliterator.OfInt>
			implements Spliterator.OfInt {

			OfInt(Spliterator.OfInt[] spliterators) {
				super(spliterators, 0, spliterators.length);
			}

			OfInt(Spliterator.OfInt[] spliterators, int fromIndex, int toIndex) {
				super(spliterators, fromIndex, toIndex);
			}

			@Override
			Spliterator.OfInt invokeTrySplit(Spliterator.OfInt spliterator) {
				return spliterator.trySplit();
			}

			@Override
			Spliterator.OfInt slice(int fromIndex, int toIndex) {
				return new ConcatSpliterator.OfInt(spliterators, fromIndex, toIndex);
			}
		}

		static final class OfLong
			extends ConcatSpliterator.OfPrimitive<
													 Long,
													 LongConsumer,
													 Spliterator.OfLong>
			implements Spliterator.OfLong {

			OfLong(Spliterator.OfLong[] spliterators) {
				super(spliterators, 0, spliterators.length);
			}

			OfLong(Spliterator.OfLong[] spliterators, int fromIndex, int toIndex) {
				super(spliterators, fromIndex, toIndex);
			}

			@Override
			Spliterator.OfLong invokeTrySplit(Spliterator.OfLong spliterator) {
				return spliterator.trySplit();
			}

			@Override
			Spliterator.OfLong slice(int fromIndex, int toIndex) {
				return new ConcatSpliterator.OfLong(spliterators, fromIndex, toIndex);
			}
		}

		static final class OfDouble
			extends ConcatSpliterator.OfPrimitive<
													 Double,
													 DoubleConsumer,
													 Spliterator.OfDouble>
			implements Spliterator.OfDouble {

			OfDouble(Spliterator.OfDouble[] spliterators) {
				super(spliterators, 0, spliterators.length);
			}

			OfDouble(Spliterator.OfDouble[] spliterators, int fromIndex, int toIndex) {
				super(spliterators, fromIndex, toIndex);
			}

			@Override
			Spliterator.OfDouble invokeTrySplit(Spliterator.OfDouble spliterator) {
				return spliterator.trySplit();
			}

			@Override
			Spliterator.OfDouble slice(int fromIndex, int toIndex) {
				return new ConcatSpliterator.OfDouble(spliterators, fromIndex, toIndex);
			}
		}
	}

	static final class ComposedClose implements Runnable {
		final BaseStream<?, ?>[] streams;

		ComposedClose(BaseStream<?, ?>[] streams) {
			this.streams = streams;
		}

		@Override
		public void run() {
			int i = 0;
			BaseStream<?, ?> stream;
			while (i < streams.length) {
				stream = streams[i++];
				try {
					stream.close();
				} catch (Throwable e1) {
					while (i < streams.length) {
						stream = streams[i++];
						try {
							stream.close();
						} catch (Throwable e2) {
							// TODO: Should we wrap this in a try/catch too?
							e1.addSuppressed(e2);
						}
					}
					throw e1;
				}
			}
		}
	}
}
// @formatter:on
