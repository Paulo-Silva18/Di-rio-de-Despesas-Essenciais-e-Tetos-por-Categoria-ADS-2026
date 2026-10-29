package br.edu.iftm.diariodespesas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Category(val label: String) {
    SUPERMERCADO("Supermercado/Feira"),
    GASOLINA("Combustível/Transporte"),
    FARMACIA("Farmácia/Saúde"),
    OUTROS("Outros")
}

enum class PaymentMethod(val label: String) {
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    CREDITO("Cartão de Crédito"),
    DEBITO("Cartão de Débito")
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val amount: Double,
    val category: String,        // Category.name
    val description: String,
    val paymentMethod: String,   // PaymentMethod.name
    val timestamp: Long
)

@Entity(tableName = "budgets", primaryKeys = ["userId", "category"])
data class Budget(
    val userId: String,
    val category: String,        // Category.name
    val limitAmount: Double
)
