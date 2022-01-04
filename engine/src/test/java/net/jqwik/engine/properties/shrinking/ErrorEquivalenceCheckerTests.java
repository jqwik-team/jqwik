package net.jqwik.engine.properties.shrinking;

import java.lang.reflect.*;
import java.util.*;

import org.mockito.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.properties.shrinking.ErrorEquivalenceCheckerTests.ExceptionType.*;

class ErrorEquivalenceCheckerTests {

	@Example
	void sameExceptionTypeInSameMethod() throws NoSuchMethodException {
		ErrorEquivalenceChecker checker = new ErrorEquivalenceChecker(throwExceptionMethod());

		Exception e1 = throwException(IAE);
		Exception e2 = throwException(IAE);

		assertThat(checker.areEquivalent(Optional.ofNullable(e1), Optional.ofNullable(e2))).isTrue();
	}

	@Example
	void differentExceptionTypeInSameMethod() throws NoSuchMethodException {
		ErrorEquivalenceChecker checker = new ErrorEquivalenceChecker(throwExceptionMethod());

		Exception e1 = throwException(IAE);
		Exception e2 = throwException(RTE);

		assertThat(checker.areEquivalent(Optional.ofNullable(e1), Optional.ofNullable(e2))).isFalse();
	}

	@Example
	void throwableWithoutStacktraceCanBeHandled() {
		ErrorEquivalenceChecker checker = new ErrorEquivalenceChecker(null);

		RuntimeException rte1 = Mockito.mock(RuntimeException.class);
		RuntimeException rte2 = Mockito.mock(RuntimeException.class);

		assertThat(checker.areEquivalent(Optional.ofNullable(rte1), Optional.ofNullable(rte2))).isTrue();
	}

	private Method throwExceptionMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("throwException", ExceptionType.class);
	}

	enum ExceptionType {
		IAE, RTE
	}

	private Exception throwException(ExceptionType type) {
		try {
			switch (type) {
				case IAE:
					throw new IllegalArgumentException("test");
				case RTE:
					throw new RuntimeException("test");
			}
		} catch (Exception e) {
			return e;
		}
		return null;
	}
}
