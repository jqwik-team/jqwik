package examples.packageWithFailings;

import net.jqwik.api.*;

public class Container1 {
	//@Property
	boolean succeed1() {
		return true;
	}


	@Property
	boolean fail1() {
		return false;
	}
}
