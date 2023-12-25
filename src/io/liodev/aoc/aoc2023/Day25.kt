package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.times

// --- 2023 Day 25: Snowverload ---
class Day25(input: String) : Day<Int> {
    override val expectedValues = listOf(54, 562772, 12_25_2023, 12_25_2023)

    private val graph = constructGraph(input.split("\n").map { it.toConnections() })

    data class Connections(val component: String, val connections: List<String>)

    private fun String.toConnections(): Connections {
        val label = this.substringBefore(':')
        val connections = this.substringAfter(": ").split(' ')
        return Connections(label, connections)
    }

    override fun solvePart1(): Int {
        var visited: Set<Int>
        do {
            getVisitedEdgesCount(graph)
                .asSequence()
                .maxBy { it.value }.key.let { (u, v) ->
                    graph[u]!!.remove(v)
                    graph[v]!!.remove(u)
                }
            visited = visitComponentOf(graph.keys.first(), graph)
        } while (visited.size == graph.keys.size)
        return visited.size * (graph.keys.size - visited.size)
    }

    private fun constructGraph(connections: List<Connections>): MutableMap<Int, MutableList<Int>> =
        buildMap<Int, MutableList<Int>> {
            connections.forEach { conn ->
                this.getOrPut(conn.component.hashCode()) { mutableListOf() }
                    .addAll(conn.connections.map { it.hashCode() })
                conn.connections.forEach { comp ->
                    this.getOrPut(comp.hashCode()) { mutableListOf() }
                        .add(conn.component.hashCode())
                }
            }
        }.toMutableMap()

    private fun getVisitedEdgesCount(graph: MutableMap<Int, MutableList<Int>>): MutableMap<Pair<Int, Int>, Int> {
        paths.clear()
        val visitedEdgesCount = mutableMapOf<Pair<Int, Int>, Int>()
        val v = graph.keys
        (v * v).filter { (a, b) -> a != b }.toSet().takeRandom(20) { (a, b) ->
            for ((v1, v2) in calculatePath(a, b, graph).zipWithNext()) {
                if (v1 < v2)
                    visitedEdgesCount[v1 to v2] = visitedEdgesCount.getOrPut(v1 to v2) { 1 } + 1
                else
                    visitedEdgesCount[v2 to v1] = visitedEdgesCount.getOrPut(v2 to v1) { 1 } + 1
            }
        }
        return visitedEdgesCount
    }

    private val paths = mutableMapOf<Pair<Int, Int>, List<Int>>()
    private fun calculatePath(a: Int, b: Int, graph: Map<Int, MutableList<Int>>) =
        if (paths[a to b] != null) paths[a to b]!!
        else if (paths[b to a] != null) paths[b to a]!!.reversed()
        else {
            val queue = ArrayDeque<Pair<Int, List<Int>>>()
            queue.add(a to listOf(a))
            val visited = mutableSetOf<Int>()
            while (queue.isNotEmpty()) {
                val (e, path) = queue.removeFirst()
                visited.add(e)
                paths[a to e] = path
                if (b in graph[e]!!) {
                    paths[a to b] = path + listOf(b)
                    break
                    // TODO make this work!
                } else if(paths[e to b] != null) {
                    paths[a to b] = path + (paths[e to b]!!).drop(1)
                    break
                } else {
                    graph[e]!!.filter { it !in visited }.forEach {
                        queue.add(it to path + listOf(it))
                    }
                }
            }
            paths[a to b]!!
        }


    private fun visitComponentOf(label: Int, graph: MutableMap<Int, MutableList<Int>>): Set<Int> {
        val queue = ArrayDeque<Int>()
        queue.add(label)
        val visited = mutableSetOf<Int>()
        while (queue.isNotEmpty()) {
            val e = queue.removeFirst()
            visited.add(e)
            queue.addAll(graph[e]!!.filter { it !in visited })
        }
        return visited
    }

    override fun solvePart2() = 12_25_2023 // MERRY CHRISTMAS!!
}

private fun <E> Set<E>.takeRandom(n: Int, function: (E) -> Unit) {
    return repeat(n) { function(this.random()) }
}

fun main() {
    val name = Day25::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day25(testInput), Day25(realInput), printTimings = true)
}