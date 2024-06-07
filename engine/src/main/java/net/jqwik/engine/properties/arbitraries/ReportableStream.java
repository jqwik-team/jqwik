package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

public class ReportableStream<T extends @Nullable Object> implements Stream<T> {

	private final List<T> values;
	private final Stream<T> stream;

	ReportableStream(List<T> list) {
		this.values = list;
		this.stream = list.stream();
	}

	public List<T> values() {
		return values;
	}

	@Override
	public String toString() {
		return String.format("%s.of(%s)", getClass().getName(), JqwikStringSupport.displayString(values));
	}

	@Override
	public Stream<T> filter(final Predicate<? super T> predicate) {
		return stream.filter(predicate);
	}

	@Override
	public <R extends @Nullable Object> Stream<R> map(final Function<? super T, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public IntStream mapToInt(final ToIntFunction<? super T> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public LongStream mapToLong(final ToLongFunction<? super T> mapper) {
		return stream.mapToLong(mapper);
	}

	@Override
	public DoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
		return stream.mapToDouble(mapper);
	}

	@Override
	public <R extends @Nullable Object> Stream<R> flatMap(final Function<? super T, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public IntStream flatMapToInt(final Function<? super T, ? extends IntStream> mapper) {
		return stream.flatMapToInt(mapper);
	}

	@Override
	public LongStream flatMapToLong(final Function<? super T, ? extends LongStream> mapper) {
		return stream.flatMapToLong(mapper);
	}

	@Override
	public DoubleStream flatMapToDouble(final Function<? super T, ? extends DoubleStream> mapper) {
		return stream.flatMapToDouble(mapper);
	}

	@Override
	public Stream<T> distinct() {
		return stream.distinct();
	}

	@Override
	public Stream<T> sorted() {
		return stream.sorted();
	}

	@Override
	public Stream<T> sorted(final Comparator<? super T> comparator) {
		return stream.sorted(comparator);
	}

	@Override
	public Stream<T> peek(final Consumer<? super T> action) {
		return stream.peek(action);
	}

	@Override
	public Stream<T> limit(final long maxSize) {
		return stream.limit(maxSize);
	}

	@Override
	public Stream<T> skip(final long n) {
		return stream.skip(n);
	}

	@Override
	public void forEach(final Consumer<? super T> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(final Consumer<? super T> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public Object[] toArray() {
		return stream.toArray();
	}

	@Override
	public <A> A[] toArray(final IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	@Override
	public T reduce(final T identity, final BinaryOperator<T> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<T> reduce(final BinaryOperator<T> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(final U identity, final BiFunction<U, ? super T, U> accumulator, final BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	@Override
	public <R extends @Nullable Object> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super T> accumulator, final BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(final Collector<? super T, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public Optional<T> min(final Comparator<? super T> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<T> max(final Comparator<? super T> comparator) {
		return stream.max(comparator);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public boolean anyMatch(final Predicate<? super T> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(final Predicate<? super T> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(final Predicate<? super T> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<T> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<T> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<T> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<T> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public Stream<T> sequential() {
		return stream.sequential();
	}

	@Override
	public Stream<T> parallel() {
		return stream.parallel();
	}

	@Override
	public Stream<T> unordered() {
		return stream.unordered();
	}

	@Override
	public Stream<T> onClose(final Runnable closeHandler) {
		return stream.onClose(closeHandler);
	}

	@Override
	public void close() {
		stream.close();
	}
}
