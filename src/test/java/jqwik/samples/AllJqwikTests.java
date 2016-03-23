
package jqwik.samples;

import org.junit.gen5.junit4.runner.Classes;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.gen5.junit4.runner.RequireEngine;
import org.junit.runner.RunWith;

@RunWith(JUnit5.class)
//@Classes({ ConstrainedParametersProperties.class })
@Classes({ IntegerProperties.class })
@RequireEngine("jqwik")
public class AllJqwikTests {
}
