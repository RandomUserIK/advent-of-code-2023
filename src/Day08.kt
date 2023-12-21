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
	fun countSteps(start: State): Int {
		var steps = 0
		var current = start
		while (current.name != "ZZZ") {
			current = transitionFunction.getValue(current).let { t ->
				when (instructions[steps++ % instructions.length]) {
					'L' -> t.left

					else -> t.right
				}
			}
		}
		return steps
	}
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

	fun part2(input: List<String>) {

	}

	val input = readInput("day08_input")
	part1(input).println()
	// part2(input).println()
}