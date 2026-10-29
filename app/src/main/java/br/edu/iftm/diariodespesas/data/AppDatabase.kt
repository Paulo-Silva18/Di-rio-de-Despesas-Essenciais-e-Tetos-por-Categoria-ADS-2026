package br.edu.iftm.diariodespesas.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userId = :uid AND timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun observeExpenses(uid: String, start: Long, end: Long): Flow<List<Expense>>

    @Query("SELECT * FROM budgets WHERE userId = :uid")
    fun observeBudgets(uid: String): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: Budget)
}

@Database(entities = [Expense::class, Budget::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "diario_despesas.db"
            ).build().also { instance = it }
        }
    }
}
