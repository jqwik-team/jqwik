package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ByteRange(min = 0, max = Byte.MAX_VALUE)
@IntRange(min = 0, max = Integer.MAX_VALUE)
@LongRange(min = 0L, max = Long.MAX_VALUE)
@FloatRange(min = 0f, max = Float.MAX_VALUE)
@DoubleRange(min = 0, max = Double.MAX_VALUE)
@Documented
public @interface Positive {
}
