package net.jqwik.kotlin.internal

import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.kotlinType
import java.lang.reflect.Parameter

// Requires KTypeEnhancer to have run before
class NullabilityEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameter: Parameter): TypeUsage {
        //val kParameter: KParameter? = parameter.kotlinParameter
        //val isNullable: Boolean = kParameter?.isMarkedNullable ?: false
        val isNullable = original.kotlinType?.isMarkedNullable ?: false
        return if (isNullable) original.asNullable() else original
    }
}