import org.jsoup.Jsoup
import org.jsoup.select.Elements
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// URL containing the results
const val RESULTS_URL = "https://uitslagensoftware.nl/resultpage.php?event_id=2021101750777&id=2946&ond=1&cat=1"

@Serializable
data class Athlete(
    val name: String,

    // Rank is nullable because there are disqualified (DQ) people
    // in the list.
    val rank: Int?,
    val bib: Int,

    // DQd may also not have finished the race, and therefore
    // not have a time.
    val grossTimeSeconds: Int?,
    val netTimeSeconds: Int?,

    // We'll parse the country if it's present, which is not often
    val country: String? = null
)

// For the countries that _are_ present in the list, map
// the title to an ISO 3166 ALPHA-3 country code
val countries = mapOf(
    "Italy" to "ITA",
    "Netherlands" to "NLD",
    "Belgium" to "BEL"
)

fun main() {
    // Download the document
    val doc = Jsoup.connect(RESULTS_URL).get()

    // Fetch the table and its children
    val tbody = doc.body().getElementsByTag("tbody")[0]
    val rows = tbody.getElementsByTag("tr")

    // Parse each row into an Athlete
    val athletes = rows.map{ parseAthlete(it.getElementsByTag("td")) }

    // Just print to stdout as JSON; I'm content with copy-pasting something once
    val json = Json.encodeToString(athletes)
    println(json)
}

fun parseAthlete(columns: Elements) : Athlete {
    // Determine the rank, null if the athlete had a DQ
    val rank = columns[0].text().let { if (it == "DQ") null else it.toInt() }

    // The name is wrapped in a hyperlink
    val name = columns[1].getElementsByTag("a")[0].text()

    // The "City" column actually contains the bib number, prefixed with
    // the Dutch word for "vest" (as in a race tanktop)
    val bib = columns[2].text().substring("Hesje ".length).toInt()

    // The country is mostly not present, but if it is, it's an image of a flag
    // with the country name in the alt and title
    val country = columns[3].getElementsByTag("img").let {
        if (it.size > 0) countries[it[0].attr("alt")] else null
    }

    val grossTimeSeconds = timeToSeconds(columns[4].text())
    val netTimeSeconds = timeToSeconds(columns[5].text())

    return Athlete(name, rank, bib, grossTimeSeconds, netTimeSeconds, country)
}

fun timeToSeconds(time: String) : Int? {
    if (time.isBlank()) return null

    return time.split(':').map { it.toInt() }.let { parts ->
        parts[0] * 3600 + parts[1] * 60 + parts[2]
    }
}