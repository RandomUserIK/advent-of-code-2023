import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.math.min

const val SEEDS = "seeds: "
const val SEED_TO_SOIL_MAP = "seed-to-soil map:"
const val SOIL_TO_FERTILIZER_MAP = "soil-to-fertilizer map:"
const val FERTILIZER_TO_WATER_MAP = "fertilizer-to-water map:"
const val WATER_TO_LIGHT_MAP = "water-to-light map:"
const val LIGHT_TO_TEMPERATURE_MAP = "light-to-temperature map:"
const val TEMPERATURE_TO_HUMIDITY_MAP = "temperature-to-humidity map:"
const val HUMIDITY_TO_LOCATION_MAP = "humidity-to-location map:"

data class Almanac(
	val seeds: List<Long>,
	val seedRanges: List<LongRange>,
	val seedToSoil: Map<LongRange, LongRange>,
	val soilToFertilizer: Map<LongRange, LongRange>,
	val fertilizerToWater: Map<LongRange, LongRange>,
	val waterToLight: Map<LongRange, LongRange>,
	val lightToTemperature: Map<LongRange, LongRange>,
	val temperatureToHumidity: Map<LongRange, LongRange>,
	val humidityToLocation: Map<LongRange, LongRange>,
) {
	fun getLocation(seed: Long): Long {
		val soil = seedToSoil.getDestination(seed)
		val fertilizer = soilToFertilizer.getDestination(soil)
		val water = fertilizerToWater.getDestination(fertilizer)
		val light = waterToLight.getDestination(water)
		val temperature = lightToTemperature.getDestination(light)
		val humidity = temperatureToHumidity.getDestination(temperature)
		return humidityToLocation.getDestination(humidity)
	}
}

fun List<String>.toAlmanac() =
	Almanac(
		seeds = first().substringAfter(SEEDS).split(" ").map { it.toLong() },
		seedRanges = first().substringAfter(SEEDS).split(" ").chunked(2).map {
			val start = it.first().toLong()
			val length = it.last().toLong()
			(start..<(start + length))
		},
		seedToSoil = sectionRanges(indexOf(SEED_TO_SOIL_MAP), indexOf(SOIL_TO_FERTILIZER_MAP)).mapSourceRangeToDestination(),
		soilToFertilizer = sectionRanges(indexOf(SOIL_TO_FERTILIZER_MAP), indexOf(FERTILIZER_TO_WATER_MAP)).mapSourceRangeToDestination(),
		fertilizerToWater = sectionRanges(indexOf(FERTILIZER_TO_WATER_MAP), indexOf(WATER_TO_LIGHT_MAP)).mapSourceRangeToDestination(),
		waterToLight = sectionRanges(indexOf(WATER_TO_LIGHT_MAP), indexOf(LIGHT_TO_TEMPERATURE_MAP)).mapSourceRangeToDestination(),
		lightToTemperature = sectionRanges(indexOf(LIGHT_TO_TEMPERATURE_MAP), indexOf(TEMPERATURE_TO_HUMIDITY_MAP)).mapSourceRangeToDestination(),
		temperatureToHumidity = sectionRanges(indexOf(TEMPERATURE_TO_HUMIDITY_MAP), indexOf(HUMIDITY_TO_LOCATION_MAP)).mapSourceRangeToDestination(),
		humidityToLocation = sectionRanges(indexOf(HUMIDITY_TO_LOCATION_MAP), size).mapSourceRangeToDestination(),
	)

fun List<String>.sectionRanges(startIndex: Int, endIndexExclusive: Int) =
	subList(startIndex + 1, endIndexExclusive).filter { it.isNotBlank() }

fun List<String>.mapSourceRangeToDestination() =
	map { it.toAlmanacRanges() }.associateBy({ it.first }, { it.second })

fun String.toAlmanacRanges() =
	split(" ").let {
		val destinationStart = it.first().toLong()
		val sourceStart = it[1].toLong()
		val length = it.last().toLong()
		(sourceStart..<(sourceStart + length)) to (destinationStart..<(destinationStart + length))
	}

fun Map<LongRange, LongRange>.getDestination(source: Long) =
	entries.firstOrNull { it.key.contains(source) }?.let {
		source + it.value.first - it.key.first
	} ?: source

fun main() {
	fun part1(input: List<String>): Long =
		input.toAlmanac().let { almanac ->
			almanac.seeds.minOf { seed ->
				almanac.getLocation(seed)
			}
		}

	fun part2(input: List<String>): Long =
		input.toAlmanac().let { almanac ->
			runBlocking(Dispatchers.Default) {
				almanac.seedRanges.map { seedRange ->
					async {
						var minLocation = Long.MAX_VALUE
						seedRange.forEach { seed ->
							minLocation = min(minLocation, almanac.getLocation(seed))
						}
						minLocation
					}
				}.awaitAll().min()
			}
		}

	val input = readInput("day05_input")
	part1(input).println()
	part2(input).println()
}