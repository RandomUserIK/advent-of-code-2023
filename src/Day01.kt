private val STR_TO_NUM = mapOf(
	"one" to "1",
	"two" to "2",
	"three" to "3",
	"four" to "4",
	"five" to "5",
	"six" to "6",
	"seven" to "7",
	"eight" to "8",
	"nine" to "9",
)

fun String.calibrationValue() =
	"${first { c -> c.isDigit() }}${last { c -> c.isDigit() }}".toInt()


fun String.getNumberAsSubstring(startingPosition: Int): String? =
	(3..5).firstNotNullOfOrNull { numberMaxLength ->
		val numberCandidate = substring(startingPosition, (startingPosition + numberMaxLength).coerceAtMost(length))

		when (STR_TO_NUM.contains(numberCandidate)) {
			true -> STR_TO_NUM.getValue(numberCandidate)

			else -> null
		}
	}

fun String.findNumber(indices: IntProgression): String {
	for (i in indices) {
		val result = when {
			this[i].isDigit() -> "${this[i]}"

			else -> getNumberAsSubstring(i)
		}

		if (result != null)
			return result
	}
	error("Number not found!")
}


fun main() {
	fun part1(input: List<String>): Int =
		input.sumOf { it.calibrationValue() }

	fun part2(input: List<String>): Int =
		input.sumOf { line ->
			val first = line.findNumber(line.indices)
			val second = line.findNumber(line.indices.reversed())

			"$first$second".calibrationValue()
		}

	val input = readInput("day01_input")
	part1(input).println()
	part2(input).println()
}
