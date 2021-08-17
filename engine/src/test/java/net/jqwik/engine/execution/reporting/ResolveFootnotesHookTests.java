package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class ResolveFootnotesHookTests {

	@BeforeTry
	void beforeTry(Footnotes footnotes) {
		footnotes.addFootnote("before try");
	}

	@AfterTry
	void afterTry(Footnotes footnotes) {
		footnotes.addFootnote("after try");
	}

	@AddLifecycleHook(CheckTries.class)
	@Property(seed = "42")
	void publishValue(@ForAll int anInt, Footnotes footnotes) {
		footnotes.addFootnote("anInt=" + anInt);
		assertThat(anInt).isLessThan(42);
	}

	static class CheckTries implements AroundTryHook {

		@Override
		public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
			TryExecutionResult result = aTry.execute(parameters);
			if (result.isFalsified()) {
				assertThat(result.footnotes()).hasSize(3);
				assertThat(result.footnotes().get(0)).isEqualTo("before try");
				assertThat(result.footnotes().get(1)).startsWith("anInt=");
				assertThat(result.footnotes().get(2)).isEqualTo("after try");
				return TryExecutionResult.satisfied();
			}
			return result;
		}

		@Override
		public int aroundTryProximity() {
			// Closer than ResolveFootnotesHook
			return -90;
		}
	}
}
