package br.edu.iftm.diariodespesas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.iftm.diariodespesas.data.*
import br.edu.iftm.diariodespesas.util.currentMonthRange
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryProgress(val category: Category, val spent: Double, val limit: Double) {
    val fraction: Float get() = if (limit > 0) (spent / limit).toFloat() else 0f
    val remaining: Double get() = limit - spent
}

data class DashboardState(
    val items: List<CategoryProgress> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val totalSpent: Double = 0.0,
    val totalLimit: Double = 0.0
) {
    val balance: Double get() = totalLimit - totalSpent
}

class MainViewModel(app: Application, private val userId: String) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dao()
    private val range = currentMonthRange()

    val dashboard: StateFlow<DashboardState> = combine(
        dao.observeExpenses(userId, range.first, range.second),
        dao.observeBudgets(userId)
    ) { expenses, budgets ->
        val limits = budgets.associate { it.category to it.limitAmount }
        val items = Category.entries.map { cat ->
            CategoryProgress(
                category = cat,
                spent = expenses.filter { it.category == cat.name }.sumOf { it.amount },
                limit = limits[cat.name] ?: 0.0
            )
        }
        DashboardState(items, expenses, items.sumOf { it.spent }, items.sumOf { it.limit })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    fun addExpense(amount: Double, category: Category, description: String, payment: PaymentMethod) {
        viewModelScope.launch {
            dao.insertExpense(
                Expense(
                    userId = userId, amount = amount, category = category.name,
                    description = description.trim(), paymentMethod = payment.name,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch { dao.deleteExpense(expense) }

    fun saveBudget(category: Category, limit: Double) {
        viewModelScope.launch { dao.upsertBudget(Budget(userId, category.name, limit)) }
    }
}

class MainViewModelFactory(private val app: Application, private val userId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(app, userId) as T
}
