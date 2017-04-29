package examples.packageWithFailings;

import net.jqwik.api.*;

public class Container2 {
	//@Property
	boolean succeed2() {
		return true;
	}


	@Property
	boolean fail2() {
		return false;
	}
}
