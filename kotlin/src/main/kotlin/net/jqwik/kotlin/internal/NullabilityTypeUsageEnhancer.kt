package net.jqwik.kotlin.internal

import net.jqwik.api.Tuple
import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

class NullabilityTypeUsageEnhancer : TypeUsage.Enhancer {

    override fun forParameter(targetType: TypeUsage, parameterInfo: Tuple.Tuple2<Parameter, Int>): TypeUsage {
        val kParameter: KParameter? = kotlinParameter(parameterInfo.get1(), parameterInfo.get2())
        val isNullable: Boolean = kParameter?.isMarkedNullable ?: false
        return if (isNullable) targetType.asNullable() else targetType
    }
}