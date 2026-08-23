package cl.comprabien.app.price

data class Retailer(
    val id: String,
    val name: String
)

data class PriceObservation(
    val productId: String,
    val retailer: Retailer,
    val price: Int,
    val referencePrice: Int? = null,
    val capturedAt: String,
    val sourceLabel: String,
    val confidence: Int
) {
    val discountPercent: Int?
        get() = referencePrice?.takeIf { it > price }?.let { reference ->
            (((reference - price).toDouble() / reference) * 100).toInt()
        }
}

class PriceRepository {
    private val lider = Retailer("r1", "Lider")
    private val jumbo = Retailer("r2", "Jumbo")
    private val tottus = Retailer("r3", "Tottus")
    private val unimarc = Retailer("r4", "Unimarc")
    private val falabella = Retailer("r5", "Falabella")
    private val paris = Retailer("r6", "Paris")
    private val ripley = Retailer("r7", "Ripley")

    // Datos ficticios: sirven solo para validar UI y lógica del Price Core.
    private val observations = listOf(
        PriceObservation("p1", lider, 2190, 2690, "Hoy 12:40", "Demo local", 100),
        PriceObservation("p1", jumbo, 2490, 2790, "Hoy 12:32", "Demo local", 100),
        PriceObservation("p1", tottus, 2290, 2590, "Hoy 12:28", "Demo local", 100),
        PriceObservation("p1", unimarc, 2390, null, "Hoy 12:20", "Demo local", 100),

        PriceObservation("p2", lider, 4990, 5790, "Hoy 12:36", "Demo local", 100),
        PriceObservation("p2", jumbo, 5290, 5790, "Hoy 12:30", "Demo local", 100),
        PriceObservation("p2", tottus, 4890, 5590, "Hoy 12:23", "Demo local", 100),

        PriceObservation("p3", lider, 1490, 1690, "Hoy 12:35", "Demo local", 100),
        PriceObservation("p3", jumbo, 1690, null, "Hoy 12:27", "Demo local", 100),
        PriceObservation("p3", unimarc, 1590, 1790, "Hoy 12:18", "Demo local", 100),

        PriceObservation("p4", lider, 8990, 10990, "Hoy 12:31", "Demo local", 100),
        PriceObservation("p4", jumbo, 9990, 11990, "Hoy 12:24", "Demo local", 100),
        PriceObservation("p4", tottus, 9490, 10490, "Hoy 12:17", "Demo local", 100),

        PriceObservation("p5", falabella, 549990, 599990, "Hoy 12:42", "Demo local", 100),
        PriceObservation("p5", paris, 559990, 629990, "Hoy 12:37", "Demo local", 100),
        PriceObservation("p5", ripley, 539990, 599990, "Hoy 12:29", "Demo local", 100),

        PriceObservation("p6", falabella, 299990, 349990, "Hoy 12:39", "Demo local", 100),
        PriceObservation("p6", paris, 319990, 349990, "Hoy 12:34", "Demo local", 100),
        PriceObservation("p6", ripley, 289990, 329990, "Hoy 12:26", "Demo local", 100),

        PriceObservation("p7", falabella, 349990, 429990, "Hoy 12:38", "Demo local", 100),
        PriceObservation("p7", paris, 369990, 449990, "Hoy 12:33", "Demo local", 100),
        PriceObservation("p7", ripley, 339990, 399990, "Hoy 12:25", "Demo local", 100),

        PriceObservation("p8", lider, 7990, 8990, "Hoy 12:21", "Demo local", 100),
        PriceObservation("p8", jumbo, 8490, null, "Hoy 12:16", "Demo local", 100),
        PriceObservation("p8", tottus, 7690, 8990, "Hoy 12:11", "Demo local", 100)
    )

    fun pricesFor(productId: String): List<PriceObservation> =
        observations.filter { it.productId == productId }.sortedBy { it.price }

    fun bestPrice(productId: String): PriceObservation? = pricesFor(productId).firstOrNull()
}
