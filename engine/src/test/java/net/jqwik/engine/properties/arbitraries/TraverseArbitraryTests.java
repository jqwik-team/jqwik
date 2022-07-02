package net.jqwik.engine.properties.arbitraries;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

class TraverseArbitraryTests {

	@Example
	void traverseWithoutRecursion(@ForAll Random random) {
		TraverseArbitrary<MyClass> arbitrary = Arbitraries.traverse(MyClass.class, new NameTraverser());

		TestingSupport.assertAllGenerated(
			arbitrary,
			random,
			myClass -> {
				assertThat(myClass.name).isEqualTo("aName");
				assertThat(myClass.tags).hasSize(3);
			}
		);
	}

	@Example
	void traverseWithRecursion(@ForAll Random random) {
		TraverseArbitrary<MyNestingClass> arbitrary =
			Arbitraries.traverse(MyNestingClass.class, new NameTraverser()).enableRecursion();

		TestingSupport.assertAllGenerated(
			arbitrary,
			random,
			myNestingClass -> {
				assertThat(myNestingClass.name).isEqualTo("aName");
				assertThat(myNestingClass.myClasses).hasSize(2);
			}
		);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Name {}

	private static class NameTraverser implements TraverseArbitrary.Traverser {
		@Override
		public Optional<Arbitrary<Object>> resolveParameter(TypeUsage parameterType) {
			if (parameterType.isOfType(String.class) && parameterType.isAnnotated(Name.class)) {
				return Optional.of(Arbitraries.just("aName"));
			}
			return Optional.empty();
		}
		@Override
		public Set<Executable> findCreators(TypeUsage targetType) {
			return Arrays.stream(targetType.getRawType().getDeclaredConstructors()).collect(CollectorsSupport.toLinkedHashSet());
		}
	}

	private static class MyNestingClass {
		final String name;
		final List<MyClass> myClasses;

		MyNestingClass(@Name String name, @Size(2) List<MyClass> myClasses) {
			this.name = name;
			this.myClasses = myClasses;
		}
	}

	private static class MyClass {
		final String name;
		final List<String> tags;

		MyClass(@Name String name, @Size(3) List<String> tags) {
			this.name = name;
			this.tags = tags;
		}
	}
}
