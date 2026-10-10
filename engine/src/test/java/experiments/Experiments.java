package experiments;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

public class Experiments {

	@Property
	void hello(@ForAll int aNumber) {
		if (aNumber > 10) {
			// fail();
		}
	}

}