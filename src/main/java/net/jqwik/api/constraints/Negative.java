package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@IntRange(min = Integer.MIN_VALUE, max = -0)
@LongRange(min = Long.MIN_VALUE, max = -0L)
@FloatRange(min = -Float.MAX_VALUE, max = -0f)
@DoubleRange(min = -Double.MAX_VALUE, max = -0d)
@Documented
public @interface Negative {
}
