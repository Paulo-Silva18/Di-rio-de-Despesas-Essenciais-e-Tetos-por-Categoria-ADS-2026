package br.edu.iftm.diariodespesas.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

private val MONEY_REGEX = Regex("^\\d{1,7}([.,]\\d{1,2})?$")
private val brazil = Locale("pt", "BR")

/** Converte texto em valor monetário. Retorna null se não numérico, negativo, zero ou grande demais. */
fun parseMoney(text: String): Double? {
    val t = text.trim()
    if (!MONEY_REGEX.matches(t)) return null
    val value = t.replace(',', '.').toDoubleOrNull() ?: return null
    return if (value > 0.0) value else null
}

/** Mantém no campo apenas dígitos e um separador decimal. */
fun filterMoneyInput(input: String): String {
    val filtered = input.filter { it.isDigit() || it == ',' || it == '.' }
    val firstSep = filtered.indexOfFirst { it == ',' || it == '.' }
    return if (firstSep == -1) filtered
    else filtered.substring(0, firstSep + 1) + filtered.substring(firstSep + 1).filter { it.isDigit() }
}

fun formatMoney(value: Double): String = NumberFormat.getCurrencyInstance(brazil).format(value)

/** Início e fim (ms) do mês corrente. */
fun currentMonthRange(): Pair<Long, Long> {
    val c = Calendar.getInstance()
    c.set(Calendar.DAY_OF_MONTH, 1)
    c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
    val start = c.timeInMillis
    c.add(Calendar.MONTH, 1)
    return start to (c.timeInMillis - 1)
}
