package br.edu.iftm.diariodespesas

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.iftm.diariodespesas.ui.*
import br.edu.iftm.diariodespesas.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) { Root() }
            }
        }
    }
}

@Composable
fun Root(authVm: AuthViewModel = viewModel()) {
    val auth by authVm.state.collectAsStateWithLifecycle()
    var registering by remember { mutableStateOf(false) }
    val uid = auth.userId

    if (uid == null) {
        AuthScreen(
            state = auth,
            isRegister = registering,
            onSubmit = { email, pass -> if (registering) authVm.register(email, pass) else authVm.login(email, pass) },
            onToggle = { registering = !registering; authVm.clearError() }
        )
    } else {
        val app = LocalContext.current.applicationContext as Application
        val vm: MainViewModel = viewModel(key = uid, factory = MainViewModelFactory(app, uid))
        val state by vm.dashboard.collectAsStateWithLifecycle()
        val nav = rememberNavController()

        NavHost(nav, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(
                    state = state,
                    onAdd = { nav.navigate("add") },
                    onBudgets = { nav.navigate("budgets") },
                    onLogout = authVm::logout,
                    onDelete = vm::deleteExpense
                )
            }
            composable("add") {
                AddExpenseScreen(
                    onSave = { amount, cat, desc, pay -> vm.addExpense(amount, cat, desc, pay); nav.popBackStack() },
                    onBack = { nav.popBackStack() }
                )
            }
            composable("budgets") {
                BudgetsScreen(
                    items = state.items,
                    onSave = vm::saveBudget,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
