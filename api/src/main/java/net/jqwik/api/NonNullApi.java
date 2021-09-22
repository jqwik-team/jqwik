package net.jqwik.api;

import javax.annotation.*;
import javax.annotation.meta.*;
import java.lang.annotation.*;

import org.apiguardian.api.*;

@Nonnull
@TypeQualifierDefault({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@API(status = API.Status.INTERNAL, since = "1.6.0")
public @interface NonNullApi {
}