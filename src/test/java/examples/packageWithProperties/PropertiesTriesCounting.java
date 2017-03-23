package examples.packageWithProperties;

import net.jqwik.api.properties.ForAll;
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

	@Override
	public void close() throws Exception {
		System.out.println("COUNTING: " + count);
	}

	enum Triade {
		Fragile, Robust, AntiFragile
	}
}
