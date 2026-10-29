package br.edu.iftm.diariodespesas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.iftm.diariodespesas.data.Category
import br.edu.iftm.diariodespesas.util.filterMoneyInput
import br.edu.iftm.diariodespesas.util.parseMoney
import br.edu.iftm.diariodespesas.viewmodel.CategoryProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(items: List<CategoryProgress>, onSave: (Category, Double) -> Unit, onBack: () -> Unit) {
    val texts: SnapshotStateMap<Category, String> = remember { mutableStateMapOf() }
    var submitted by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    // Preenche com o teto atual apenas uma vez por categoria.
    LaunchedEffect(items) {
        items.forEach { if (it.category !in texts) texts[it.category] = if (it.limit > 0) "%.2f".format(it.limit).replace('.', ',') else "" }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Tetos mensais") }, navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } })
    }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Category.entries.forEach { cat ->
                val text = texts[cat] ?: ""
                OutlinedTextField(
                    value = text,
                    onValueChange = { texts[cat] = filterMoneyInput(it); saved = false },
                    label = { Text("Teto — ${cat.label} (R$)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = submitted && parseMoney(text) == null,
                    supportingText = { if (submitted && parseMoney(text) == null) Text("Valor numérico maior que zero.") }
                )
            }
            if (saved) Text("Tetos salvos!")
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                submitted = true
                val parsed = Category.entries.associateWith { parseMoney(texts[it] ?: "") }
                if (parsed.values.all { it != null }) {
                    parsed.forEach { (c, v) -> onSave(c, v!!) }
                    saved = true
                }
            }) { Text("Salvar tetos") }
        }
    }
}
