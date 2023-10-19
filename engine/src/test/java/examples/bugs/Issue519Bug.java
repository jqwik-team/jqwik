package examples.bugs;

import net.jqwik.api.*;

// See https://github.com/jqwik-team/jqwik/issues/519
class Issue519Bug implements C<MyRecord> {
	@Provide("anyT")
	@Override
	public Arbitrary<MyRecord> anyT() {
		return Arbitraries.of(new MyRecord());
	}
}

interface A<T> {
	@Property
	default boolean test1(@ForAll("anyT") T t) {
		return true;
	}

	Arbitrary<T> anyT();
}

interface B<T extends Comparable<? super T>> extends A<T> {
	@Property
	default boolean test2(@ForAll("anyT") T t) {
		return true;
	}
}

interface MyType extends Comparable<MyType> { }

interface C<T extends MyType> extends B<T> {
	@Property
	default boolean test3(@ForAll("anyT") T t) {
		return true;
	}
}

class MyRecord implements MyType {
	@Override
	public int compareTo(MyType o) {
		return 0;
	}
}

