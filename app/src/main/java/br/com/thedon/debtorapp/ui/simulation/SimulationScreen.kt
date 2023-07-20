package br.com.thedon.debtorapp.ui.simulation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.debtorapp.DebtorTopAppBar
import br.com.thedon.debtorapp.R
import br.com.thedon.debtorapp.ui.AppViewModelProvider
import br.com.thedon.debtorapp.ui.debtor.DebtorDetails
import br.com.thedon.debtorapp.ui.debtor.toDebtor
import br.com.thedon.debtorapp.ui.navigation.NavigationDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object SimulationDestination : NavigationDestination {
    override val route = "simulation"
    override val titleRes = R.string.simulation_title
    const val debtorNameArg = "debtorName"
    const val debtorDebtArg = "debtorDebt"
    val routeWithArgs = "$route/{$debtorNameArg}/{$debtorDebtArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SimulationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var simulationJob: Job? by remember {
        mutableStateOf(null)
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            DebtorTopAppBar(
                title = stringResource(SimulationDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        SimulationBody(
            modifier = modifier.padding(innerPadding),
            simulationUiState = viewModel.simulationUiState,
            onSimulationParamsChange = viewModel::updateUiState,
            enabled = !viewModel.simulationUiState.simulationFlags.isSimulationStarted,
            onStartSimulation = {
                simulationJob = coroutineScope.launch {
                    viewModel.simulate()
                }
            },
            onStopSimulation = {
                simulationJob?.cancel()
                viewModel.cancelSimulation()
            }

        )

    }
}

@Composable
fun SimulationBody(
    simulationUiState: SimulationUiState,
    onSimulationParamsChange: (SimulationParams) -> Unit,
    modifier: Modifier = Modifier,
    onStartSimulation: () -> Unit,
    onStopSimulation: () -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DebtorDisplayDetails(
            debtorDetails = simulationUiState.debtorDetails
        )
        Divider()
        SimulationForm(
            simulationParams = simulationUiState.simulationParams,
            onValueChange = onSimulationParamsChange,
            enabled = enabled
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Button(
                onClick = onStartSimulation,
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(id = R.string.simulation_play_icon)
                )
                Text(text = stringResource(id = R.string.simulation_play))
            }
            Button(
                onClick = onStopSimulation,
                enabled = !enabled
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_stop),
                    contentDescription = stringResource(id = R.string.simulation_play_icon)
                )
                Text(text = stringResource(id = R.string.simulation_stop))
            }
        }
        Divider()
        SimulationResults(
            paymentResults = simulationUiState.paymentResults,
            simulationStarted = simulationUiState.simulationFlags.isSimulationStarted,
            simulationFinished = simulationUiState.simulationFlags.isSimulationFinished,
        )

    }
}

@Composable
fun SimulationResults(
    paymentResults: PaymentResults,
    simulationStarted: Boolean,
    simulationFinished: Boolean,
) {
    var emoji = stringResource(id = R.string.simulation_initial_emoji)
    var title = stringResource(id = R.string.simulation_initial_title)
    var message = stringResource(id = R.string.simulation_initial_message)

    if (simulationFinished) {
        emoji = stringResource(id = R.string.simulation_finished_emoji)
        title = stringResource(id = R.string.simulation_finished_title)
        message = stringResource(
            id = R.string.simulation_finished_message,
            NumberFormat.getCurrencyInstance().format(paymentResults.totalInterestPaid),
            paymentResults.totalTime
        )
    } else if (simulationStarted) {
        emoji = stringResource(id = R.string.simulation_progress_emoji)
        title = stringResource(id = R.string.simulation_progress_title)
        message = stringResource(id = R.string.simulation_progress_message)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = emoji, style = MaterialTheme.typography.displayLarge)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationForm(
    simulationParams: SimulationParams,
    onValueChange: (SimulationParams) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = "Simulation Params", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = simulationParams.interestRate,
                onValueChange = { onValueChange(simulationParams.copy(interestRate = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.simulation_interest_req)) },
                leadingIcon = { Text("%") },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
            OutlinedTextField(
                value = simulationParams.repaymentSpeed,
                onValueChange = { onValueChange(simulationParams.copy(repaymentSpeed = it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(stringResource(R.string.simulation_repayment_req)) },
                leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true
            )
        }
    }
}

@Composable
fun DebtorDisplayDetails(debtorDetails: DebtorDetails) {
    val debtor = debtorDetails.toDebtor()
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.simulation_debtor, debtor.name),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(
                R.string.simulation_totalDebt,
                NumberFormat.getCurrencyInstance().format(debtor.debt)
            ),
            style = MaterialTheme.typography.titleLarge

        )
    }

}
