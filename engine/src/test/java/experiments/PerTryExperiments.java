package experiments;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class PerTryExperiments {

	int test = 1;

	@Property
	@PerTry(MyPerTry.class)
	boolean anotherFailure(@ForAll int anInt) {
		try (TryLifecycle lifecycle = new TryLifecycle() {

		}) {
			assertThat(anInt).isLessThan(0);
			return true;
		}
	}

	@interface PerTry {
		Class<? extends TryLifecycle> value();
	}

	class MyPerTry extends TryLifecycle {
		@Override
		public void before() {
			System.out.println(test);
			System.out.println(this.getParameters());
		}

		@Override
		public void onFailure(Throwable throwable) {}
	}

	static class TryLifecycle implements AutoCloseable {

		private static final List<Object> UNKNOWN_PARAMETERS = new AbstractList<Object>() {
			@Override
			public int size() {
				throw new JqwikException("You are using the lifecycle outside its context");
			}

			@Override
			public Object get(int index) {
				throw new JqwikException("You are using the lifecycle outside its context");
			}
		};

		private List<Object> parameters;

		public TryLifecycle() {
			parameters = UNKNOWN_PARAMETERS;
			before();
		}

		public List<Object> changeParameters(List<Object> parameters) {
			return parameters;
		}

		public List<Object> getParameters() {
			return parameters;
		}

		public void before() {

		}

		public void onFailure(Throwable t) {

		}

		@Override
		public final void close() {

		}
	}
}