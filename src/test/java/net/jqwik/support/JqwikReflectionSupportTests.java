package net.jqwik.support;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.stream.*;

@Group
class JqwikReflectionSupportTests {

	@Group
	class NewInstanceWithDefaultConstructor {

		@Example
		boolean staticClass() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(Outer.class) instanceof Outer;
		}

		@Example
		boolean innerClassWithoutConstructor() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(Outer.Inner.class) instanceof Outer.Inner;
		}

		@Example
		boolean innerClassWithDefaultConstructor() {
			return JqwikReflectionSupport
					.newInstanceWithDefaultConstructor(Outer.InnerWithConstructor.class) instanceof Outer.InnerWithConstructor;
		}

		@Example
		boolean staticClassWithDefaultConstructor() {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(OuterWithConstructor.class) instanceof OuterWithConstructor;
		}

	}

	@Example
	void streamInnerInstances() {
		Outer outer = new Outer();
		Outer.Inner inner = outer.createInner();

		Stream<Object> instances = JqwikReflectionSupport.streamInnerInstances(inner);

		Assertions.assertThat(instances).containsExactly(inner, outer);
	}

	private static class Outer {

		Inner createInner() {
			return new Inner();
		}

		class Inner {

		}

		class InnerWithConstructor {
			private final String aString;
			public InnerWithConstructor() {
				this.aString = "hallo";
			}
		}
	}

	private static class OuterWithConstructor {

	}
}
