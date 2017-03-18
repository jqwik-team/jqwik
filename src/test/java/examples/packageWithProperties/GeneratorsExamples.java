package examples.packageWithProperties;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Generate;
import net.jqwik.api.properties.Generator;
import net.jqwik.api.properties.Property;

public class GeneratorsExamples {

	@Property(tries = 5)
	boolean aString(@ForAll(size = 10) String aString, @ForAll(value = "digitsOnly", size = 10) String anotherString) {
		System.out.println(String.format("#%s# #%s#", aString, anotherString));
		return true;
	}

	@Generate
	Arbitrary<String> stringArbitrary() {
		return Generator.string('a', 'z');
	}

	@Generate
	Arbitrary<String> digitsOnly() {
		return Generator.string('0', '9');
	}
}
