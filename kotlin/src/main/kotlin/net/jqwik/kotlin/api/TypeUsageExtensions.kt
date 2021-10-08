package net.jqwik.kotlin.api

import net.jqwik.api.providers.TypeUsage
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

internal val kTypeMetaInfoKey: String get() = KType::class.jvmName

val TypeUsage.kotlinType: KType?
    get() {
        val metaInfo = this.getMetaInfo(kTypeMetaInfoKey)
        return if (metaInfo.isPresent) metaInfo.get() as KType else null
    }