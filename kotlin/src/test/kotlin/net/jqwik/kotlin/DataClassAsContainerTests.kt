package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.lifecycle.*
import org.assertj.core.api.Assertions
import java.util.*

@LifecycleHooks(
    AddLifecycleHook(ResolveName::class),
    AddLifecycleHook(ResolveAge::class)
)
data class DataClassAsContainerTests(val name: String, val age: Int) {
    @Property(tries = 10)
    fun aProperty(@ForAll randomString: String) {
        Assertions.assertThat(name).isEqualTo("a Name")
        Assertions.assertThat(age).isEqualTo(42)
    }
}

class ResolveName : ResolveParameterHook {
    override fun resolve(
        parameterContext: ParameterResolutionContext,
        lifecycleContext: LifecycleContext
    ): Optional<ResolveParameterHook.ParameterSupplier> {
        if (parameterContext.typeUsage().isOfType(String::class.java)) {
            return Optional.of(ResolveParameterHook.ParameterSupplier{ ignore -> "a Name"})
        }
        return Optional.empty()
    }
}

class ResolveAge : ResolveParameterHook {
    override fun resolve(
        parameterContext: ParameterResolutionContext,
        lifecycleContext: LifecycleContext
    ): Optional<ResolveParameterHook.ParameterSupplier> {
        if (parameterContext.typeUsage().isOfType(Int::class.java)) {
            return Optional.of(ResolveParameterHook.ParameterSupplier{ ignore -> 42})
        }
        return Optional.empty()
    }
}