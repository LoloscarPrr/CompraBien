package cl.comprabien.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.comprabien.app.catalog.CatalogRepository
import cl.comprabien.app.catalog.Product
import cl.comprabien.app.catalog.ProductCategory
import cl.comprabien.app.price.PriceObservation
import cl.comprabien.app.price.PriceRepository
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CompraBienApp() }
    }
}

private enum class Screen(val title: String, val description: String) {
    HOME("CompraBien", "Antes de comprar, mira CompraBien."),
    SEARCH("Buscar", "Encuentra productos y compara precios demo entre tiendas."),
    SCAN("Escanear", "Escanea un código de barras para saber si el precio conviene."),
    LIST("Mi lista", "Organiza tu compra y compara el costo total de tu canasta."),
    DEALS("Ofertas reales", "Descubre rebajas según historial de precios, no solo el descuento anunciado.")
}

@Composable
fun CompraBienApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    BackHandler(enabled = screen != Screen.HOME || selectedProduct != null) {
        if (selectedProduct != null) selectedProduct = null else screen = Screen.HOME
    }
    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 20.dp)
            when {
                selectedProduct != null -> ProductPriceScreen(selectedProduct!!, { selectedProduct = null }, modifier)
                screen == Screen.HOME -> HomeScreen({ screen = it }, modifier)
                screen == Screen.SEARCH -> SearchScreen({ screen = Screen.HOME }, { selectedProduct = it }, modifier)
                else -> FeatureScreen(screen, { screen = Screen.HOME }, modifier)
            }
        }
    }
}

@Composable
private fun HomeScreen(onNavigate: (Screen) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp)); Text("CompraBien", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp)); Text("Antes de comprar, mira CompraBien.")
        Spacer(Modifier.height(28.dp)); Button({ onNavigate(Screen.SEARCH) }, Modifier.fillMaxWidth()) { Text("Buscar producto") }
        Spacer(Modifier.height(12.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeAction("Escanear", Modifier.weight(1f)) { onNavigate(Screen.SCAN) }; HomeAction("Mi lista", Modifier.weight(1f)) { onNavigate(Screen.LIST) }
        }
        Spacer(Modifier.height(12.dp)); HomeAction("Ofertas reales", Modifier.fillMaxWidth()) { onNavigate(Screen.DEALS) }
        Spacer(Modifier.height(28.dp)); Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) {
            Text("Historial de precios activo", fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp))
            Text("CompraBien ya compara el precio actual con un historial DEMO para estimar si una rebaja es realmente conveniente.")
        }}
    }
}

@Composable
private fun SearchScreen(onBack: () -> Unit, onOpenProduct: (Product) -> Unit, modifier: Modifier = Modifier) {
    val catalog = remember { CatalogRepository() }; val prices = remember { PriceRepository() }
    var query by remember { mutableStateOf("") }; var category by remember { mutableStateOf<ProductCategory?>(null) }
    val results = catalog.search(query, category)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(18.dp)); Text("Buscar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("DATOS DEMO", fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text("Prueba: café, arroz, celular, PS5, TV o arena.")
        Spacer(Modifier.height(16.dp)); OutlinedTextField(query, { query = it }, label = { Text("Producto, marca o categoría") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp)); Text("Categoría", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp)); OutlinedButton({ category = null }, Modifier.fillMaxWidth()) { Text(if (category == null) "✓ Todas" else "Todas") }
        catalog.categories().forEach { c -> Spacer(Modifier.height(6.dp)); OutlinedButton({ category = c }, Modifier.fillMaxWidth()) { Text(if (category == c) "✓ ${c.label}" else c.label) } }
        Spacer(Modifier.height(20.dp)); Text("${results.size} resultado(s)", fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp))
        results.forEach { p -> ProductCard(p, prices.bestPrice(p.id)) { onOpenProduct(p) }; Spacer(Modifier.height(10.dp)) }
        if (results.isEmpty()) Card(Modifier.fillMaxWidth()) { Text("No encontramos productos para esa búsqueda.", Modifier.padding(18.dp)) }
        Spacer(Modifier.height(18.dp)); OutlinedButton(onBack) { Text("Volver") }; Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProductCard(product: Product, best: PriceObservation?, onOpen: () -> Unit) {
    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
        Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("${product.brand} • ${product.presentation}"); Text(product.category.label)
        Spacer(Modifier.height(10.dp)); if (best != null) { Text("Mejor precio demo: ${clp(best.price)}", fontWeight = FontWeight.Bold); Text("${best.retailer.name} • ${best.capturedAt}"); Spacer(Modifier.height(8.dp)); Button(onOpen, Modifier.fillMaxWidth()) { Text("Comparar precios e historial") } } else Text("Sin precios demo disponibles")
    }}
}

@Composable
private fun ProductPriceScreen(product: Product, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val repo = remember { PriceRepository() }; val observations = repo.pricesFor(product.id)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(18.dp)); Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("${product.brand} • ${product.presentation}")
        Spacer(Modifier.height(8.dp)); Text("DATOS DEMO — no son precios comerciales reales", fontWeight = FontWeight.Bold); Spacer(Modifier.height(18.dp))
        observations.forEachIndexed { index, obs ->
            PriceCard(obs, index == 0); val summary = repo.historySummary(product.id, obs.retailer.id); val series = repo.historyFor(product.id, obs.retailer.id)
            if (summary != null) { Spacer(Modifier.height(6.dp)); Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
                Text("¿Es una oferta real?", fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text(summary.verdict)
                Text("Precio habitual demo: ${clp(summary.usualPrice)}"); Text("Mínimo histórico demo: ${clp(summary.historicalMin)}"); Text("Máximo histórico demo: ${clp(summary.historicalMax)}")
                val changeLabel = if (summary.changeVsUsualPercent <= 0) "${-summary.changeVsUsualPercent}% bajo lo habitual" else "${summary.changeVsUsualPercent}% sobre lo habitual"
                Text("Hoy: $changeLabel", fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text("Historial 6 meses", fontWeight = FontWeight.Bold)
                series.forEach { point -> Text("${point.period}: ${clp(point.price)}") }
            }}}
            Spacer(Modifier.height(14.dp))
        }
        if (observations.isEmpty()) Card(Modifier.fillMaxWidth()) { Text("Todavía no hay observaciones de precio para este producto.", Modifier.padding(18.dp)) }
        OutlinedButton(onBack) { Text("Volver a resultados") }; Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PriceCard(o: PriceObservation, isBest: Boolean) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
    Text(o.retailer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(clp(o.price), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    if (isBest) Text("Mejor precio demo"); o.referencePrice?.let { Text("Referencia: ${clp(it)}"); o.discountPercent?.let { d -> Text("Baja anunciada calculada: $d%") } }
    Spacer(Modifier.height(6.dp)); Text("Actualización: ${o.capturedAt}"); Text("Fuente: ${o.sourceLabel} • confianza ${o.confidence}%")
} } }

private fun clp(value: Int): String = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(value)
@Composable private fun HomeAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) { OutlinedButton(onClick, modifier) { Text(label) } }
@Composable private fun FeatureScreen(screen: Screen, onBack: () -> Unit, modifier: Modifier = Modifier) { Column(modifier.fillMaxSize()) { Spacer(Modifier.height(24.dp)); Text(screen.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text(screen.description); Spacer(Modifier.height(24.dp)); Card(Modifier.fillMaxWidth()) { Text("Base funcional preparada para su módulo correspondiente.", Modifier.padding(18.dp)) }; Spacer(Modifier.height(20.dp)); OutlinedButton(onBack) { Text("Volver") } } }
@Preview(showBackground = true) @Composable private fun PreviewApp() { CompraBienApp() }
