const val CARD_INFO_DELIMITER = ":"
const val WHITESPACE = " "
const val NUMBERS_DELIMITER = "|"

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


fun List<Int>.getCopiesCount(start: Int, end: Int): Int {
	var count = 0
	(start..end).forEach { i ->
		count += this[i] + getCopiesCount(i + 1, i + this[i])
	}
	return count
}


fun main() {
	fun part1(input: List<String>): Int =
		input
			.map {
				it.toCard().let { card -> (card.winningNumbers intersect card.obtainedNumbers).size }
			}
			.filter { it > 0 }
			.sumOf { 1.shl(it - 1) }


	fun part2(input: List<String>): Int {
		val cardNumbers = input
			.map {
				it.toCard().let { card -> (card.winningNumbers intersect card.obtainedNumbers).size }
			}

		var count = 0
		(0..cardNumbers.lastIndex).forEach { i ->
			count += 1 + cardNumbers[i] + cardNumbers.getCopiesCount(i + 1, i + cardNumbers[i])
		}
		return count
	}

	val input = readInput("day04_input")
	part1(input).println()
	part2(input).println()
}