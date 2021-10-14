package net.jqwik.kotlin.api

import net.jqwik.api.providers.TypeUsage
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

internal val kTypeMetaInfoKey: String = KType::class.jvmName

val TypeUsage.kotlinType: KType?
    get() {
        val metaInfo = this.getMetaInfo(kTypeMetaInfoKey)
        return if (metaInfo.isPresent) metaInfo.get() as KType else null
    }

fun TypeUsage.isAssignableFrom(kClass: KClass<*>) : Boolean = isAssignableFrom(kClass.java)