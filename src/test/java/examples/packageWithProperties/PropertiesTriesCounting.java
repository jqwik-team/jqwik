package examples.packageWithProperties;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Generate;
import net.jqwik.api.properties.Generator;
import net.jqwik.api.properties.Property;

public class PropertiesTriesCounting implements AutoCloseable {

	int count = 0;

	@Property
	boolean noParam() {
		count++;
		return true;
	}

	@Property
	boolean oneIntParam(@ForAll int a) {
		count++;
		return true;
	}

	@Property
	boolean oneEnumParam(@ForAll Triade t) {
		count++;
		return true;
	}

	@Property
	boolean twoEnumParams(@ForAll("notFragile") Triade t1, @ForAll("notFragile") Triade t2) {
		count++;
		return true;
	}

	@Generate
	Arbitrary<Triade> notFragile() {
		return Generator.of(Triade.class).filter(t -> t != Triade.Fragile);
	}


	@Override
	public void close() throws Exception {
		System.out.println("COUNTING: " + count);
	}

	enum Triade {
		Fragile, Robust, AntiFragile
	}
}
