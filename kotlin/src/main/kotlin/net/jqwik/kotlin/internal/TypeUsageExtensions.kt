package net.jqwik.kotlin.internal

import net.jqwik.api.Tuple
import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter
import java.util.*

fun TypeUsage.nonNull() : TypeUsage {
    if (!this.parameterInfo.isPresent) {
        return this
    }
    return WithoutParameterInfo(this)
}

private class WithoutParameterInfo(val nullableType : TypeUsage) : TypeUsage by nullableType {

    override fun getParameterInfo(): Optional<Tuple.Tuple2<Parameter, Int>> {
        return Optional.empty()
    }
}