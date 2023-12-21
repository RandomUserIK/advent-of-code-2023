private const val DEFAULT_CARD_STRENGTH = "23456789TJQKA"
private const val JOKER_CARD_STRENGTH = "J23456789TQKA"
private const val JOKER = 'J'

enum class HandType(
	val cardCounts: List<Int>,
	val strength: Int,
) {
	FIVE_OF_A_KIND(listOf(5), 6),
	FOUR_OF_A_KIND(listOf(4, 1), 5),
	FULL_HOUSE(listOf(3, 2), 4),
	THREE_OF_A_KIND(listOf(3, 1, 1), 3),
	TWO_PAIR(listOf(2, 2, 1), 2),
	ONE_PAIR(listOf(2, 1, 1, 1), 1),
	HIGH_CARD(listOf(1, 1, 1, 1, 1), 0);

	companion object {
		fun from(cardCounts: List<Int>) =
			when (cardCounts) {
				FIVE_OF_A_KIND.cardCounts -> FIVE_OF_A_KIND
				FOUR_OF_A_KIND.cardCounts -> FOUR_OF_A_KIND
				FULL_HOUSE.cardCounts -> FULL_HOUSE
				THREE_OF_A_KIND.cardCounts -> THREE_OF_A_KIND
				TWO_PAIR.cardCounts -> TWO_PAIR
				ONE_PAIR.cardCounts -> ONE_PAIR
				HIGH_CARD.cardCounts -> HIGH_CARD

				else -> error("Unknown hand type!")
			}

		fun typeWithJokers(cardCounts: Map<Char, Int>): HandType {
			val jokerCount = cardCounts[JOKER] ?: 0
			return when {
				jokerCount == 0 -> from(cardCounts.values.sortedDescending())
				jokerCount > 0 && cardCounts.size == 1 -> from(listOf(jokerCount))
				else -> {
					val countsWithoutJokers = cardCounts.entries.filter { it.key != JOKER }.associate { it.toPair() }
					from(
						buildMap {
							putAll(countsWithoutJokers)
							countsWithoutJokers.maxBy { it.value }.let { put(it.key, it.value + jokerCount) }
						}.values.sortedDescending()
					)
				}
			}
		}
	}
}

class CamelCardHand(
	val cards: String,
	val bid: Int,
	val handType: HandType,
	val useJokers: Boolean,
) : Comparable<CamelCardHand> {
	override fun compareTo(other: CamelCardHand): Int {
		val cardStrength = if (useJokers) JOKER_CARD_STRENGTH else DEFAULT_CARD_STRENGTH
		return when {
			handType != other.handType -> handType.strength.compareTo(other.handType.strength)

			else -> cards
				.zip(other.cards)
				.first { (a, b) -> a != b }
				.let { (a, b) -> cardStrength.indexOf(a).compareTo(cardStrength.indexOf(b)) }
		}
	}
}

fun List<String>.toHands(handTypeTransform: (cards: String) -> HandType, useJokers: Boolean) = map { it.toHand(handTypeTransform, useJokers) }

fun String.toHand(handTypeTransform: (cards: String) -> HandType, useJokers: Boolean) =
	split(" ").let {
		CamelCardHand(
			cards = it.first(),
			bid = it.last().toInt(),
			handType = handTypeTransform(it.first()),
			useJokers = useJokers,
		)
	}

fun List<CamelCardHand>.winnings() =
	sorted().withIndex().sumOf { (idx, hand) -> (idx + 1) * hand.bid }

fun main() {
	fun part1(input: List<String>) =
		input
			.toHands({ cards -> HandType.from(cards.groupingBy { it }.eachCount().values.sortedDescending()) }, false)
			.winnings()

	fun part2(input: List<String>) =
		input
			.toHands({ cards -> HandType.typeWithJokers(cards.groupingBy { it }.eachCount()) }, true)
			.winnings()

	val input = readInput("day07_input")
	part1(input).println()
	part2(input).println()
}