package net.jqwik.kotlin

import net.jqwik.api.Example
import net.jqwik.api.Tuple
import net.jqwik.kotlin.api.*
import org.assertj.core.api.Assertions.assertThat

class JqwikTupleTests {

    @Example
    fun `Tuple2 has components`() {
        val (a, b) = Tuple.of(1, 2)
        assertThat(a).isEqualTo(1)
        assertThat(b).isEqualTo(2)
    }

    @Example
    fun `Tuple3 has components`() {
        val (a, b, c) = Tuple.of(1, 2, 3)
        assertThat(c).isEqualTo(3)
    }

    @Example
    fun `Tuple4 has components`() {
        val (a, b, c, d) = Tuple.of(1, 2, 3, 4)
        assertThat(d).isEqualTo(4)
    }

    @Example
    fun `Tuple5 has components`() {
        val (a, b, c, d, e) = Tuple.of(1, 2, 3, 4, 5)
        assertThat(e).isEqualTo(5)
    }

    @Example
    fun `Tuple6 has components`() {
        val (a, b, c, d, e, f) = Tuple.of(1, 2, 3, 4, 5, 6)
        assertThat(f).isEqualTo(6)
    }

    @Example
    fun `Tuple7 has components`() {
        val (a, b, c, d, e, f, g) = Tuple.of(1, 2, 3, 4, 5, 6, 7)
        assertThat(g).isEqualTo(7)
    }

    @Example
    fun `Tuple8 has components`() {
        val (a, b, c, d, e, f, g, h) = Tuple.of(1, 2, 3, 4, 5, 6, 7, 8)
        assertThat(h).isEqualTo(8)
    }
}