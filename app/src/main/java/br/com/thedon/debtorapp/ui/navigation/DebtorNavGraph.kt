package br.com.thedon.debtorapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.thedon.debtorapp.ui.debtor.DebtorEditDestination
import br.com.thedon.debtorapp.ui.debtor.DebtorEditScreen
import br.com.thedon.debtorapp.ui.debtor.DebtorEntryDestination
import br.com.thedon.debtorapp.ui.debtor.DebtorEntryScreen
import br.com.thedon.debtorapp.ui.home.HomeDestination
import br.com.thedon.debtorapp.ui.home.HomeScreen
import br.com.thedon.debtorapp.ui.simulation.SimulationDestination
import br.com.thedon.debtorapp.ui.simulation.SimulationScreen

@Composable
fun DebtorNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToItemEntry = { navController.navigate(DebtorEntryDestination.route) },
                navigateToItemUpdate = {
                    navController.navigate("${DebtorEditDestination.route}/${it}")
                }
            )
        }

        composable(route = DebtorEntryDestination.route) {
            DebtorEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = DebtorEditDestination.routeWithArgs,
            arguments = listOf(navArgument(DebtorEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            DebtorEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToSimulation = {
                    navController.navigate("${SimulationDestination.route}/${it.name}/${it.debt}")
                }
            )
        }

        composable(
            route = SimulationDestination.routeWithArgs,
            arguments = listOf(
                navArgument(SimulationDestination.debtorNameArg) { type = NavType.StringType },
                navArgument(SimulationDestination.debtorDebtArg) { type = NavType.StringType }
            )
        ) {
            SimulationScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}