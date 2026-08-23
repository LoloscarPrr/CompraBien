package cl.comprabien.app.catalog

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: ProductCategory,
    val presentation: String,
    val gtin: String? = null,
    val keywords: List<String> = emptyList()
)

enum class ProductCategory(val label: String) {
    SUPERMARKET("Supermercado"),
    TECHNOLOGY("Tecnología"),
    HOME("Hogar"),
    GAMING("Gaming"),
    BEAUTY("Belleza"),
    PETS("Mascotas")
}
