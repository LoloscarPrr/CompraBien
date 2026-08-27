package cl.comprabien.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import cl.comprabien.app.ui.theme.*
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
    SEARCH("Buscar", "Encuentra productos y compara precios."),
    SCAN("Escanear", "Escanea un código de barras para saber si el precio conviene."),
    LIST("Mi lista", "Organiza tu compra y compara el costo total de tu canasta."),
    DEALS("Ofertas reales", "Descubre rebajas según historial de precios.")
}

@Composable
fun CompraBienApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    BackHandler(enabled = screen != Screen.HOME || selectedProduct != null) {
        if (selectedProduct != null) selectedProduct = null else screen = Screen.HOME
    }

    CompraBienTheme {
        Scaffold(containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) { innerPadding ->
            val modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 16.dp)
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
private fun DemoBadge() {
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(100.dp)) {
        Text("DEMO", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HomeScreen(onNavigate: (Screen) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        Spacer(Modifier.height(22.dp))
        Text("CompraBien", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Decide rápido si realmente conviene.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        Button({ onNavigate(Screen.SEARCH) }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Buscar producto") }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeAction("Escanear", Modifier.weight(1f)) { onNavigate(Screen.SCAN) }
            HomeAction("Mi lista", Modifier.weight(1f)) { onNavigate(Screen.LIST) }
        }
        Spacer(Modifier.height(12.dp))
        HomeAction("Ofertas reales", Modifier.fillMaxWidth()) { onNavigate(Screen.DEALS) }
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("Historial de precios activo", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("CompraBien compara el precio de hoy con su comportamiento anterior.")
            }
        }
    }
}

@Composable
private fun SearchScreen(onBack: () -> Unit, onOpenProduct: (Product) -> Unit, modifier: Modifier = Modifier) {
    val catalog = remember { CatalogRepository() }
    val prices = remember { PriceRepository() }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<ProductCategory?>(null) }
    val results = catalog.search(query, category)

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Buscar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            DemoBadge()
        }
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            query, { query = it },
            placeholder = { Text("Ej: café, PS5, TV…") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))
        Text("Categorías", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        CategoryGrid(catalog.categories(), category) { category = it }

        Spacer(Modifier.height(22.dp))
        Text(if (results.isEmpty()) "Sin resultados" else "${results.size} resultado(s)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        results.forEach { product ->
            ProductCard(product, prices.bestPrice(product.id)) { onOpenProduct(product) }
            Spacer(Modifier.height(12.dp))
        }
        if (results.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Prueba con un nombre más general, por ejemplo “café” en vez de “café 500 g”.", Modifier.padding(18.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        TextButton(onBack) { Text("← Volver") }
        Spacer(Modifier.height(22.dp))
    }
}

@Composable
private fun CategoryGrid(categories: List<ProductCategory>, selected: ProductCategory?, onSelect: (ProductCategory?) -> Unit) {
    val all: List<ProductCategory?> = listOf(null) + categories
    all.chunked(2).forEach { rowItems ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            rowItems.forEach { item ->
                val active = selected == item
                if (active) {
                    Button({ onSelect(item) }, Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text(item?.label ?: "Todas") }
                } else {
                    FilledTonalButton({ onSelect(item) }, Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text(item?.label ?: "Todas") }
                }
            }
            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ProductCard(product: Product, best: PriceObservation?, onOpen: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("${product.brand} • ${product.presentation}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            if (best != null) {
                Text(clp(best.price), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Mejor precio demo • ${best.retailer.name}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Button(onOpen, Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Ver si conviene") }
            } else Text("Sin precios demo disponibles")
        }
    }
}

@Composable
private fun ProductPriceScreen(product: Product, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val repo = remember { PriceRepository() }
    val observations = repo.pricesFor(product.id)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(14.dp))
        Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("${product.brand} • ${product.presentation}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp)); DemoBadge(); Spacer(Modifier.height(18.dp))

        observations.forEachIndexed { index, obs ->
            val summary = repo.historySummary(product.id, obs.retailer.id)
            val series = repo.historyFor(product.id, obs.retailer.id)
            DealCard(obs, index == 0, summary?.changeVsUsualPercent, summary?.verdict, summary?.usualPrice, summary?.historicalMin, summary?.historicalMax, series.map { it.period to it.price })
            Spacer(Modifier.height(14.dp))
        }
        TextButton(onBack) { Text("← Volver a resultados") }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DealCard(
    observation: PriceObservation,
    isBest: Boolean,
    changeVsUsual: Int?,
    verdict: String?,
    usual: Int?,
    min: Int?,
    max: Int?,
    history: List<Pair<String, Int>>
) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when {
        changeVsUsual == null -> MaterialTheme.colorScheme.secondaryContainer
        changeVsUsual <= -8 -> CompraGreenSoft
        changeVsUsual < 0 -> CompraAmberSoft
        else -> CompraRedSoft
    }
    val statusText = when {
        changeVsUsual == null -> "Sin historial"
        changeVsUsual <= -8 -> "Buena compra"
        changeVsUsual < 0 -> "Bajó un poco"
        else -> "No conviene"
    }

    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(observation.retailer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(clp(observation.price), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Surface(color = statusColor, shape = RoundedCornerShape(100.dp)) {
                    Text(statusText, Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                }
            }
            if (isBest) {
                Spacer(Modifier.height(8.dp))
                Text("★ Mejor precio encontrado", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            if (changeVsUsual != null) {
                Spacer(Modifier.height(10.dp))
                val quick = if (changeVsUsual <= 0) "${-changeVsUsual}% bajo lo habitual" else "$changeVsUsual% sobre lo habitual"
                Text(quick, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            verdict?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            Spacer(Modifier.height(10.dp))
            TextButton({ expanded = !expanded }) { Text(if (expanded) "Ocultar detalles" else "Ver historial y detalles") }

            if (expanded) {
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                usual?.let { MetricRow("Precio habitual", clp(it)) }
                min?.let { MetricRow("Mínimo", clp(it)) }
                max?.let { MetricRow("Máximo", clp(it)) }
                observation.referencePrice?.let { MetricRow("Referencia tienda", clp(it)) }
                Spacer(Modifier.height(10.dp))
                Text("Historial demo", fontWeight = FontWeight.Bold)
                history.forEach { (period, price) -> MetricRow(period, clp(price)) }
                Spacer(Modifier.height(8.dp))
                Text("Actualizado ${observation.capturedAt} • ${observation.sourceLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
    Spacer(Modifier.height(5.dp))
}

private fun clp(value: Int): String = NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(value)

@Composable
private fun HomeAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(onClick, modifier, shape = RoundedCornerShape(16.dp)) { Text(label) }
}

@Composable
private fun FeatureScreen(screen: Screen, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        Text(screen.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(screen.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.fillMaxWidth()) {
            Text("Esta sección se activará en las siguientes versiones.", Modifier.padding(18.dp))
        }
        Spacer(Modifier.height(20.dp)); TextButton(onBack) { Text("← Volver") }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewApp() { CompraBienApp() }

// Build marker: v0.4.1
