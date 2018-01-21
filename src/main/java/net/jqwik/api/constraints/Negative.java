package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ShortRange(min = Short.MIN_VALUE, max = -0)
@ByteRange(min = Byte.MIN_VALUE, max = -0)
@IntRange(min = Integer.MIN_VALUE, max = -0)
@LongRange(min = Long.MIN_VALUE, max = -0L)
@FloatRange(min = -Float.MAX_VALUE, max = -0f)
@DoubleRange(min = -Double.MAX_VALUE, max = -0d)
@Documented
public @interface Negative {
}
