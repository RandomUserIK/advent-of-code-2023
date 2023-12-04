const val MAX_RED = 12
const val MAX_GREEN = 13
const val MAX_BLUE = 14

const val GAME_INFO_DELIMITER = ":"
const val RED = "red"
const val BLUE = "blue"
const val GREEN = "green"

data class GameBag(
	val id: Int = 0,
	val blue: Int = 0,
	val green: Int = 0,
	val red: Int = 0,
)

fun String.toBag(): GameBag =
	split(GAME_INFO_DELIMITER).let { (gameInfo, gameContents) ->
		gameContents.let {
			GameBag(
				id = gameInfo.gameId(),
				blue = it.getCountByColor(BLUE),
				red = it.getCountByColor(RED),
				green = it.getCountByColor(GREEN),
			)
		}
	}

fun String.gameId(): Int = filter { it.isDigit() }.toInt()

fun String.getCountByColor(color: String): Int =
	"\\d+ $color".toRegex().findAll(this).map { it.value.filter { c -> c.isDigit() }.toInt() }.max()

fun main() {
	fun part1(input: List<String>): Int =
		input
			.map { it.toBag() }
			.filter {
				it.red <= MAX_RED && it.blue <= MAX_BLUE && it.green <= MAX_GREEN
			}
			.sumOf { it.id }


	fun part2(input: List<String>): Int =
		input
			.map { it.toBag() }
			.sumOf { it.red * it.blue * it.green }

	val input = readInput("day02_input")
	part1(input).println()
	part2(input).println()
}
