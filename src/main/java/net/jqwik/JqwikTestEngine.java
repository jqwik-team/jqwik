package net.jqwik;

import net.jqwik.recording.*;
import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final TestRunData testRunData = new TestRunData();
	private final TestRunRecorder recorder = TestRunRecorder.NULL;
	//	private final TestRunRecorder recorder = new TestRunRecorder() {
	//	@Override
	//	public void record(TestRun testRun) {
	//		System.out.println(testRun.toString());
	//	}
	//};

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(testRunData).discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		new JqwikExecutor(registry, recorder).execute(request, root);
	}

}
