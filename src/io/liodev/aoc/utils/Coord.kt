package io.liodev.aoc.utils

data class Coord(val r: Int, val c: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun getBorder(length: Int = 1): List<Coord> {
        return buildList {
            add(Coord(r, c - 1))
            repeat(length + 2) {
                add(Coord(r - 1, c - 1 + it))
                add(Coord(r + 1, c - 1 + it))
            }
            add(Coord(r, c + length))
        }
    }

    override fun toString(): String {
        return "$r,$c"
    }

    fun validIndex(array: List<List<Char>>): Boolean {
        return validIndex(array.size, array[0].size)
    }

    private fun validIndex(w: Int, h: Int) = r in 0 until h && c in 0 until w

    operator fun plus(other: Coord): Coord = Coord(this.r + other.r, this.c + other.c)

    fun Pair<Int, Int>.toCoord() = Coord(this)

    fun List<List<Any?>>.validIndex(coord: Coord) =
        coord.r in this.indices && coord.c in this[0].indices

    operator fun <E> MutableList<MutableList<E>>.set(coordinates: Coord, value: E) {
        this[coordinates.r][coordinates.c] = value
    }

    operator fun <E> List<List<E>>.get(coordinates: Coord): E {
        return this[coordinates.r][coordinates.c]
    }
}


fun <E> MutableList<MutableList<E>>.printMatrix() {
    this.forEach { row ->
        println(row.joinToString("") { it.toString() })
    }
}

/**
 * Returns the Cartesian product of this [IntRange] with another [IntRange].
 *
 * @param other The other [IntRange] to compute the Cartesian product with.
 * @return A list of pairs representing the Cartesian product.
 */
operator fun IntRange.times(other: IntRange): List<Pair<Int, Int>> =
    this.flatMap { x -> other.map { y -> x to y } }

/**
 * Returns the Cartesian product of this [Collection] with another [Iterable].

 * @param other The other [Iterable] to compute the Cartesian product with.
 * @return A list of pairs representing the Cartesian product.
 */
operator fun <T, S> Collection<T>.times(other: Iterable<S>): List<Pair<T, S>> {
    return cartesianProduct(other) { first, second -> first to second }
}

/**
 * Computes the Cartesian product of this [Collection] with another [Iterable],
 * applying a transformation to each pair of elements.

 * @param other The other [Iterable] to compute the Cartesian product with.
 * @param transformer A function that takes a pair of elements (first from this [Collection] and
 *                    second from [other]) and transforms them into a value of type [V].
 * @return A list of values representing the Cartesian product after applying the transformer function.
 */
fun <T, S, V> Collection<T>.cartesianProduct(
    other: Iterable<S>,
    transformer: (first: T, second: S) -> V
): List<V> {
    return this.flatMap { first -> other.map { second -> transformer.invoke(first, second) } }
}
