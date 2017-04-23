package net.jqwik.execution.pipeline;

import net.jqwik.*;
import org.junit.platform.engine.*;

public class CircularTaskDependencyException extends JqwikException {

	public CircularTaskDependencyException(UniqueId id) {
		super(String.format("Circular dependency involving execution task [%s].", id));
	}
}
