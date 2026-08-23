package cl.comprabien.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.comprabien.app.catalog.CatalogRepository
import cl.comprabien.app.catalog.Product
import cl.comprabien.app.catalog.ProductCategory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CompraBienApp() }
    }
}

private enum class Screen(val title: String, val description: String) {
    HOME("CompraBien", "Antes de comprar, mira CompraBien."),
    SEARCH("Buscar", "Encuentra productos del catálogo de CompraBien."),
    SCAN("Escanear", "Escanea un código de barras para saber si el precio conviene."),
    LIST("Mi lista", "Organiza tu compra y compara el costo total de tu canasta."),
    DEALS("Ofertas reales", "Descubre rebajas según historial de precios, no solo el descuento anunciado.")
}

@Composable
fun CompraBienApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    BackHandler(enabled = screen != Screen.HOME) { screen = Screen.HOME }

    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onNavigate = { screen = it },
                    modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 20.dp)
                )
                Screen.SEARCH -> SearchScreen(
                    onBack = { screen = Screen.HOME },
                    modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 20.dp)
                )
                else -> FeatureScreen(
                    screen = screen,
                    onBack = { screen = Screen.HOME },
                    modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(onNavigate: (Screen) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("CompraBien", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Antes de comprar, mira CompraBien.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = { onNavigate(Screen.SEARCH) }, modifier = Modifier.fillMaxWidth()) { Text("Buscar producto") }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeAction("Escanear", Modifier.weight(1f)) { onNavigate(Screen.SCAN) }
            HomeAction("Mi lista", Modifier.weight(1f)) { onNavigate(Screen.LIST) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HomeAction("Ofertas reales", Modifier.fillMaxWidth()) { onNavigate(Screen.DEALS) }

        Spacer(modifier = Modifier.height(28.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Catálogo v0.2 activo", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("El buscador ya trabaja con productos y categorías. Los precios reales llegan en Price Core.")
            }
        }
    }
}

@Composable
private fun SearchScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val repository = remember { CatalogRepository() }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    val results = repository.search(query, selectedCategory)

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(18.dp))
        Text("Buscar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Prueba: café, arroz, celular, PS5, TV o arena.")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Producto, marca o categoría") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Categoría", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { selectedCategory = null }, modifier = Modifier.fillMaxWidth()) {
            Text(if (selectedCategory == null) "✓ Todas" else "Todas")
        }
        repository.categories().forEach { category ->
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(onClick = { selectedCategory = category }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedCategory == category) "✓ ${category.label}" else category.label)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("${results.size} resultado(s)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        results.forEach { product ->
            ProductCard(product)
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (results.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("No encontramos productos para esa búsqueda.", modifier = Modifier.padding(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        OutlinedButton(onClick = onBack) { Text("Volver") }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProductCard(product: Product) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${product.brand} • ${product.presentation}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.category.label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HomeAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
}

@Composable
private fun FeatureScreen(screen: Screen, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(screen.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(screen.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("Base funcional preparada para su módulo correspondiente.", modifier = Modifier.padding(18.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(onClick = onBack) { Text("Volver") }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() { CompraBienApp() }
