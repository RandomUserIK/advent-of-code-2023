import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

data class Race(
	val time: Long,
	val distance: Long,
) {
	fun waysToBeatDistance() =
		(0..time).map {
			(time - it) * it
		}.filter { it > distance }

	fun waysToBeatDistanceOptimized(): Int {
		// total time  -> t
		// distance -> d
		// time pressed -> x
		// (t - x) * x = d => -x^2 + tx - d = 0
		val x1 = (-time - sqrt(time * time - 4.0 * distance)) / -2.0
		val x2 = (-time + sqrt(time * time - 4.0 * distance)) / -2.0
		return (ceil(x1) - (floor(x2) + 1)).toInt()
	}
}

fun List<String>.toRaces() =
	first().substringAfter("Time: ").split(" ").filter { it.isNotBlank() }.map { it.toLong() }
		.zip(
			last().substringAfter("Distance: ").split(" ").filter { it.isNotBlank() }.map { it.toLong() }
		)
		.map {
			Race(
				time = it.first,
				distance = it.second,
			)
		}

fun List<String>.toRace() =
	Race(
		time = first().substringAfter("Time: ").split(" ").filter { it.isNotBlank() }.joinToString("").toLong(),
		distance = last().substringAfter("Distance: ").split(" ").filter { it.isNotBlank() }.joinToString("").toLong(),
	)

fun main() {
	fun part1(input: List<String>) =
		input
			.toRaces()
			.map { it.waysToBeatDistance().size }
			.reduce(Int::times)

	fun part2(input: List<String>) =
		input
			.toRace()
			.waysToBeatDistanceOptimized()

	val input = readInput("day06_input")
	part1(input).println()
	part2(input).println()
}