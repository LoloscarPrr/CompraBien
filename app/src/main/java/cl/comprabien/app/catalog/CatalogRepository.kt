package cl.comprabien.app.catalog

class CatalogRepository {
    private val products = listOf(
        Product("p1", "Coca-Cola Original", "Coca-Cola", ProductCategory.SUPERMARKET, "2 L", keywords = listOf("bebida", "coca", "cola")),
        Product("p2", "Nescafé Tradición", "Nescafé", ProductCategory.SUPERMARKET, "170 g", keywords = listOf("cafe", "café", "instantaneo", "instantáneo")),
        Product("p3", "Arroz Grado 1", "Tucapel", ProductCategory.SUPERMARKET, "1 kg", keywords = listOf("arroz", "grano")),
        Product("p4", "Detergente Líquido", "Omo", ProductCategory.SUPERMARKET, "3 L", keywords = listOf("detergente", "lavado", "ropa")),
        Product("p5", "PlayStation 5 Slim", "Sony", ProductCategory.GAMING, "1 unidad", keywords = listOf("ps5", "playstation", "consola")),
        Product("p6", "Galaxy A56 5G", "Samsung", ProductCategory.TECHNOLOGY, "128 GB", keywords = listOf("celular", "telefono", "teléfono", "smartphone")),
        Product("p7", "Smart TV 55 pulgadas", "Samsung", ProductCategory.TECHNOLOGY, "55\"", keywords = listOf("televisor", "tv", "smart tv")),
        Product("p8", "Arena Sanitaria", "Champion Cat", ProductCategory.PETS, "10 kg", keywords = listOf("gato", "arena", "mascota"))
    )

    fun search(query: String, category: ProductCategory? = null): List<Product> {
        val normalized = query.trim().lowercase()
        return products.filter { product ->
            val categoryMatches = category == null || product.category == category
            val textMatches = normalized.isBlank() || listOf(
                product.name,
                product.brand,
                product.presentation,
                product.category.label
            ).plus(product.keywords).any { it.lowercase().contains(normalized) }
            categoryMatches && textMatches
        }
    }

    fun categories(): List<ProductCategory> = ProductCategory.entries
}
