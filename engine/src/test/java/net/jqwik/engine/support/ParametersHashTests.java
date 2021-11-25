package net.jqwik.engine.support;

import java.lang.reflect.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class ParametersHashTests {

	@Example
	void isEqualForSameParameters() {
		ParametersHash hash1 = new ParametersHash(methodNamed("method1"));
		ParametersHash hash2 = new ParametersHash(methodNamed("method2"));
		assertThat(hash1).isEqualTo(hash2);
	}

	@Example
	void isDifferentForDifferentParameters() {
		ParametersHash hash1 = new ParametersHash(methodNamed("method1"));
		ParametersHash hash3 = new ParametersHash(methodNamed("method3"));
		assertThat(hash1).isNotEqualTo(hash3);
	}

	@Example
	void isDifferentForDifferentOrderOfParameters() {
		ParametersHash hash3 = new ParametersHash(methodNamed("method3"));
		ParametersHash hash4 = new ParametersHash(methodNamed("method4"));
		assertThat(hash3).isNotEqualTo(hash4);
	}

	@Example
	void isDifferentForDifferentParameterAnnotations() {
		ParametersHash hash1 = new ParametersHash(methodNamed("method1"));
		ParametersHash hash5 = new ParametersHash(methodNamed("method5"));
		assertThat(hash1).isNotEqualTo(hash5);
	}

	@Example
	void isEqualForSameParameterAnnotations() {
		ParametersHash hash5 = new ParametersHash(methodNamed("method5"));
		ParametersHash hash6 = new ParametersHash(methodNamed("method6"));
		assertThat(hash5).isEqualTo(hash6);
	}

	@Example
	void isDifferentForSameAnnotationsButDifferentAnnotationAttributes() {
		ParametersHash hash5 = new ParametersHash(methodNamed("method5"));
		ParametersHash hash7 = new ParametersHash(methodNamed("method7"));
		assertThat(hash5).isNotEqualTo(hash7);
	}

	@Example
	void methodMatches() {
		ParametersHash hash1 = new ParametersHash(methodNamed("method1"));
		assertThat(hash1.matchesMethod(methodNamed("method2"))).isTrue();
	}

	@Example
	void methodDoesNotMatch() {
		ParametersHash hash1 = new ParametersHash(methodNamed("method1"));
		assertThat(hash1.matchesMethod(methodNamed("method3"))).isFalse();
	}

	private Method methodNamed(String name) {
		return Arrays.stream(TestMethods.class.getMethods())
					 .filter(m -> m.getName().equals(name))
					 .findFirst().orElseThrow(() -> {
				String message = String.format("Method named %s does not exist.", name);
				return new RuntimeException(message);
			});
	}

	private static class TestMethods {

		public void method1(String aString) {}

		public void method2(String aString) {}

		public void method3(String aString, int anInt) {}

		public void method4(int anInt, String aString) {}

		public void method5(@ForAll String aString) {}

		public void method6(@ForAll String aString) {}

		public void method7(@ForAll("poops") String aString) {}
	}
}
