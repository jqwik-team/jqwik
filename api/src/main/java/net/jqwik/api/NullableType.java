package net.jqwik.api;

import javax.annotation.*;
import javax.annotation.meta.*;
import java.lang.annotation.*;

import org.apiguardian.api.*;

/**
 * This annotation is necessary because {@linkplain Nullable} cannot be applied to type usages
 */
@Nonnull(when = When.MAYBE)
@TypeQualifierDefault({ElementType.TYPE_USE})
@Target({ElementType.TYPE_USE})
@API(status = API.Status.INTERNAL, since = "1.6.0")
public @interface NullableType {
}