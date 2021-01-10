package examples.packageWithErrors;

import net.jqwik.api.*;

public class ContainerWithOOME {

	@Property(tries = 10)
	void throwOutOfMemoryError(@ForAll int i) {
		throw new OutOfMemoryError();
	}
}
