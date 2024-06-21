package net.jqwik.kotlin

import net.jqwik.api.Arbitraries
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.arbitraries.*
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.array
import net.jqwik.kotlin.api.ofSize
import net.jqwik.testing.TestingSupport.checkAllGenerated
import java.util.*
import kotlin.streams.toList


class SizableArbitraryExtensionsTests {

    @Example
    fun `ListArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: ListArbitrary<Int> = Int.any().list().ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable -> sizable.size in 2..12 }
    }

    @Example
    fun `SetArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: SetArbitrary<Int> = Int.any().set().ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable -> sizable.size in 2..12 }
    }

    @Example
    fun `ArrayArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: ArrayArbitrary<Int, IntArray> = Int.any().array<Int, IntArray>().ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable -> sizable.size in 2..12 }
    }

    @Example
    fun `StreamArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: StreamArbitrary<Int> = Int.any().stream().ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable -> sizable.count() in 2..12 }
    }

    @Example
    fun `IteratorArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: IteratorArbitrary<Int> = Int.any().iterator().ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable ->
            val list = sizable.asSequence().toList()
            list.size in 2..12
        }
    }

    @Example
    fun `MapArbitrary ofSize() with range`(@ForAll random: Random) {
        val sizableArbitrary: MapArbitrary<Int, String> =
            Arbitraries.maps(Int.any(), String.any()).ofSize(2..12)
        checkAllGenerated(sizableArbitrary, random) { sizable ->
            sizable.size in 2..12
        }
    }

}