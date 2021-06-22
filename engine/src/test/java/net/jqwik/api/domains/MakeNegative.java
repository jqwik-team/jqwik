package net.jqwik.api.domains;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface MakeNegative {}
