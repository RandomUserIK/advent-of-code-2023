const val DOT = '.'
const val GEAR = '*'

data class Point2D(
	val x: Int,
	val y: Int,
) {
	val neighborhood: Set<Point2D>
		get() = setOf(
			Point2D(x = x, y = y - 1),
			Point2D(x = x, y = y + 1),

			Point2D(x = x - 1, y = y),
			Point2D(x = x + 1, y = y),

			Point2D(x = x - 1, y = y - 1),
			Point2D(x = x - 1, y = y + 1),

			Point2D(x = x + 1, y = y - 1),
			Point2D(x = x + 1, y = y + 1),
		)
}

data class Symbol(
	val value: Char,
	val location: Point2D,
)

data class Number(
	val digits: MutableList<Char> = mutableListOf(),
	val neighborhood: MutableSet<Point2D> = mutableSetOf(),
) {
	val value: Int
		get() = digits.joinToString("").toInt()
}

data class EngineSchematic(
	val numbers: MutableList<Number> = mutableListOf(),
	val symbols: MutableList<Symbol> = mutableListOf(),
) {
	val symbolLocations: Set<Point2D>
		get() = symbols.map { it.location }.toSet()
}

fun List<String>.toEngineSchematic(canTakeSymbol: (Char) -> Boolean): EngineSchematic {
	val engineSchematic = EngineSchematic()

	forEachIndexed { row, line ->
		var number = Number()
		line.forEachIndexed { col, c ->
			if (c.isDigit()) {
				number.digits.add(c)
				number.neighborhood.addAll(Point2D(row, col).neighborhood)
			} else {
				if (number.digits.isNotEmpty()) {
					engineSchematic.numbers.add(number)
					number = Number()
				}

				if (canTakeSymbol(c)) {
					engineSchematic.symbols.add(Symbol(c, Point2D(row, col)))
				}
			}
		}
		if (number.digits.isNotEmpty()) {
			engineSchematic.numbers.add(number)
		}
	}

	return engineSchematic
}

fun main() {
	fun part1(input: List<String>): Int =
		input
			.toEngineSchematic { it != DOT }
			.let {
				it.numbers
					.filter { number -> number.neighborhood.intersect(it.symbolLocations).isNotEmpty() }
					.sumOf { number -> number.value }
			}

	fun part2(input: List<String>): Int =
		input
			.toEngineSchematic { it == GEAR }
			.let {
				it.symbols
					.mapNotNull { symbol ->
						val numbers = it.numbers.filter { number -> symbol.location in number.neighborhood }
						when {
							numbers.size == 2 -> numbers.first().value * numbers.last().value
							else -> null
						}
					}
					.sum()
			}

	val input = readInput("day03_input")
	part1(input).println()
	part2(input).println()
}