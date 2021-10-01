package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A shrunk falsified sample is a {@linkplain FalsifiedSample} that results
 * from shrinking.
 *
 * @see PropertyExecutionResult#originalSample()
 */
@API(status = EXPERIMENTAL, since = "1.3.5")
public interface ShrunkFalsifiedSample extends FalsifiedSample {

	/**
	 * @return number of steps needed to shrink from original sample to this one
	 */
	int countShrinkingSteps();
}
