package br.edu.iftm.diariodespesas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.iftm.diariodespesas.data.*
import br.edu.iftm.diariodespesas.util.formatMoney
import br.edu.iftm.diariodespesas.viewmodel.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Green = Color(0xFF2E7D32)
private val Yellow = Color(0xFFF9A825)
private val Red = Color(0xFFC62828)

/** Verde (< 75%) -> Amarelo (75% a 99%) -> Vermelho (>= 100%, teto estourado). */
fun progressColor(fraction: Float): Color = when {
    fraction >= 1f -> Red
    fraction >= 0.75f -> Yellow
    else -> Green
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onAdd: () -> Unit,
    onBudgets: () -> Unit,
    onLogout: () -> Unit,
    onDelete: (Expense) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário de Despesas") },
                actions = {
                    TextButton(onClick = onBudgets) { Text("Tetos") }
                    TextButton(onClick = onLogout) { Text("Sair") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd) { Text("+ Compra") }
        }
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SummaryCard(state) }
            items(state.items, key = { it.category.name }) { CategoryCard(it) }
            item { Text("Compras do mês", style = MaterialTheme.typography.titleMedium) }
            if (state.expenses.isEmpty()) item { Text("Nenhuma compra lançada neste mês.") }
            items(state.expenses, key = { it.id }) { ExpenseRow(it, onDelete) }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(state: DashboardState) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Resumo do mês", style = MaterialTheme.typography.titleMedium)
            Text("Total gasto: ${formatMoney(state.totalSpent)}")
            Text("Soma dos tetos: ${formatMoney(state.totalLimit)}")
            val negative = state.balance < 0
            Text(
                "Saldo disponível: ${formatMoney(state.balance)}",
                fontWeight = FontWeight.Bold,
                color = if (negative) Red else Green
            )
        }
    }
}

@Composable
private fun CategoryCard(item: CategoryProgress) {
    val color = progressColor(item.fraction)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.category.label, fontWeight = FontWeight.SemiBold)
                if (item.limit > 0) Text("${(item.fraction * 100).toInt()}%", color = color, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { item.fraction.coerceIn(0f, 1f) },
                color = color,
                modifier = Modifier.fillMaxWidth().height(10.dp)
            )
            if (item.limit <= 0) {
                Text("Defina um teto em \"Tetos\".", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("${formatMoney(item.spent)} de ${formatMoney(item.limit)}", style = MaterialTheme.typography.bodySmall)
                if (item.remaining < 0) {
                    Text("Teto estourado em ${formatMoney(-item.remaining)}", color = Red, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("Resta ${formatMoney(item.remaining)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(e: Expense, onDelete: (Expense) -> Unit) {
    val date = SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(Date(e.timestamp))
    val cat = Category.valueOf(e.category).label
    val pay = PaymentMethod.valueOf(e.paymentMethod).label
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(e.description.ifBlank { cat }, fontWeight = FontWeight.Medium)
            Text("$date • $cat • $pay", style = MaterialTheme.typography.bodySmall)
        }
        Text(formatMoney(e.amount), fontWeight = FontWeight.Bold)
        TextButton(onClick = { onDelete(e) }) { Text("Excluir") }
    }
}
