private const val LEFT = 'L'
private const val START = 'A'
private const val END = 'Z'

data class State(
	val name: String,
)

data class Transition(
	val left: State,
	val right: State,
)

class DFA(
	val instructions: String,
	val transitionFunction: Map<State, Transition>,
) {
	fun countSteps(start: State): Long {
		var steps = 0
		var current = start
		while (!current.name.endsWith(END)) {
			current = transitionFunction.getValue(current).let { t ->
				when (instructions[steps++ % instructions.length]) {
					LEFT -> t.left

					else -> t.right
				}
			}
		}
		return steps.toLong()
	}

	fun stepsToReachAllEndingNodes(): Long =
		transitionFunction
			.keys
			.filter { it.name.endsWith(START) }
			.map { countSteps(it) }
			.reduce { a, b -> lcm(a, b) }

	private tailrec fun gcd(a: Long, b: Long): Long {
		return if (b == 0L) a
		else gcd(b, a % b)
	}

	private fun lcm(a: Long, b: Long) = (a * b) / gcd(a, b)
}

fun List<String>.toDFA() =
	DFA(
		instructions = first(),
		transitionFunction = drop(2).associate {
			State(it.substringBefore(" =")) to Transition(
				left = State(it.substringAfter("(").substringBefore(",")),
				right = State(it.substringAfter(", ").substringBefore(")"))
			)
		}
	)

fun main() {
	fun part1(input: List<String>) =
		input
			.toDFA()
			.countSteps(State("AAA"))

	fun part2(input: List<String>) =
		input
			.toDFA()
			.stepsToReachAllEndingNodes()

	val input = readInput("day08_input")
	part1(input).println()
	part2(input).println()
}