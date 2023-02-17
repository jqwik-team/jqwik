package examples.bugs;

import java.util.stream.*;

import net.jqwik.api.*;

/**
 * https://github.com/jqwik-team/jqwik/issues/180
 */
public class MaxEdgeCasesBelowZeroBug {

	@Property
	void testEdgeCases(@ForAll("edgeCases") Object arg1,
					   @ForAll("edgeCases") Object arg2) { // 2 args are required for repro
	}

	@Provide("edgeCases")
	Arbitrary<Object> generate() {
		return
			Builders.withBuilder(Object::new)
					   .use(Arbitraries.strings()
									   .edgeCases(c -> c.add(IntStream.range(0, 1000) // generate lots of edge cases
																	  .mapToObj(String::valueOf)
																	  .collect(Collectors.toList())
																	  .toArray(new String[0]))))
					   .in((a, b) -> a)
					   .use(Arbitraries.bytes().list().list()) // single list() gets max edge cases down to 0, list().list() reaches -1
					   .in((a, b) -> a)
					   .build();
	}

}
