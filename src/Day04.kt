const val CARD_INFO_DELIMITER = ":"
const val WHITESPACE = " "
const val NUMBERS_DELIMITER = "|"

const val TEST = """Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"""

data class Card(
	val winningNumbers: Set<Int>,
	val obtainedNumbers: Set<Int>,
)

fun String.toCard() =
	split(CARD_INFO_DELIMITER).let { (_, numbers) ->
		Card(
			winningNumbers = numbers
				.substringBefore(NUMBERS_DELIMITER)
				.split(WHITESPACE)
				.filter { it.isNotBlank() }
				.map { it.toInt() }.toSet(),
			obtainedNumbers = numbers
				.substringAfter(NUMBERS_DELIMITER)
				.split(WHITESPACE)
				.filter { it.isNotBlank() }
				.map { it.toInt() }.toSet(),
		)
	}

fun main() {
	fun part1(input: List<String>): Int =
		input
			.map {
				it.toCard().let { card -> (card.winningNumbers intersect card.obtainedNumbers).size }
			}
			.sumOf {
				1.shl(it - 1)
			}


	fun part2(input: List<String>): Int =
		TODO()

	val input = readInput("day04_input")
	part1(input).println()
	// part2(TEST.lines()).println()
}