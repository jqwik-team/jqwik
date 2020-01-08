package examples.packageWithFailings;

import net.jqwik.api.*;

public class ContainerWithStatistics {

	@Property(tries = 100, generation = GenerationMode.RANDOMIZED)
	void propertyWithStatistics(@ForAll boolean aBool) {
		Statistics.collect(aBool);
	}
}
