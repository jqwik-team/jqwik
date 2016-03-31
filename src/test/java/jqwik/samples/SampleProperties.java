
package jqwik.samples;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import net.jqwik.api.Assumptions;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.junit4.runner.JUnit5;
import org.junit.runner.RunWith;
import org.opentest4j.AssertionFailedError;

import java.util.Date;

@RunWith(JUnit5.class)
public class SampleProperties {

	@Property
    boolean succeedingPropertyWithoutParamOnlyCalledOnce() {
		return true;
	}

	@Property
    static boolean succeedingStaticProperty(int aNumber) {
	    return Math.abs(aNumber) >= 0;
	}

	@Property
	boolean failingProperty(Date aDate) {
		return aDate.before(new Date());
	}

	@Property
	boolean rootOfAllPositiveSquaresShouldBeOriginalNumber(@InRange(maxInt = 46000, minInt = -10) int anInteger) {
		Assumptions.assume(anInteger >= 0);
		return Math.sqrt(anInteger * anInteger) == anInteger;
	}

	@Property(trials = 55)
	void stringAndNumberCanBeCombined(String aString, Long aNumber) {
		String combined = aString + aNumber;
		Assertions.assertEquals(aString + Long.toString(aNumber), combined);
	}

}
