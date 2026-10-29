package br.edu.iftm.diariodespesas.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.iftm.diariodespesas.data.Category
import br.edu.iftm.diariodespesas.data.PaymentMethod
import br.edu.iftm.diariodespesas.util.filterMoneyInput
import br.edu.iftm.diariodespesas.util.parseMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onSave: (Double, Category, String, PaymentMethod) -> Unit,
    onBack: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<Category?>(null) }
    var payment by remember { mutableStateOf<PaymentMethod?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val amount = parseMoney(amountText)

    Scaffold(topBar = {
        TopAppBar(title = { Text("Nova compra") }, navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } })
    }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = amountText, onValueChange = { amountText = filterMoneyInput(it) },
                label = { Text("Valor (R$)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = submitted && amount == null,
                supportingText = { if (submitted && amount == null) Text("Informe um valor numérico maior que zero.") }
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it.take(60) },
                label = { Text("Descrição / estabelecimento") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Text("Categoria", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Category.entries.forEach { c ->
                    FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c.label) })
                }
            }
            if (submitted && category == null) Text("Selecione uma categoria.", color = MaterialTheme.colorScheme.error)

            Text("Forma de pagamento", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.entries.forEach { p ->
                    FilterChip(selected = payment == p, onClick = { payment = p }, label = { Text(p.label) })
                }
            }
            if (submitted && payment == null) Text("Selecione a forma de pagamento.", color = MaterialTheme.colorScheme.error)

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    submitted = true
                    val c = category; val p = payment
                    if (amount != null && c != null && p != null) onSave(amount, c, description, p)
                }
            ) { Text("Salvar compra") }
        }
    }
}
