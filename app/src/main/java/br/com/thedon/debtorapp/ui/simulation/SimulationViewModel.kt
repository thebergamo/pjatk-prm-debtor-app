package br.com.thedon.debtorapp.ui.simulation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.debtorapp.ui.debtor.DebtorDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val interestRate = "2.5"
const val repaymentSpeed = "0.25"

class SimulationViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var isSimulationStarted by mutableStateOf(false)
        private set
    var isSimulationFinished by mutableStateOf(false)
        private set
    var isSimulationCancelled by mutableStateOf(false)
        private set

    var simulationUiState by mutableStateOf(
        SimulationUiState()
    )
        private set

    private val debtorName: String =
        checkNotNull(savedStateHandle[SimulationDestination.debtorNameArg])
    private var debtorDebt: String =
        checkNotNull(savedStateHandle[SimulationDestination.debtorDebtArg])

    init {
        viewModelScope.launch {
            simulationUiState =
                SimulationUiState(
                    debtorDetails = DebtorDetails(name = debtorName, debt = debtorDebt),
                    simulationParams = SimulationParams(
                        interestRate, repaymentSpeed
                    ),
                    simulationFlags = SimulationFlags(
                        isSimulationStarted = isSimulationStarted,
                        isSimulationFinished = isSimulationFinished,
                        isSimulationCancelled = isSimulationCancelled
                    )
                )
        }
    }

    fun updateUiState(simulationParams: SimulationParams) {
        simulationUiState = simulationUiState.copy(
            simulationParams = simulationParams
        )
    }

    suspend fun simulate() {
        startSimulation()

        val repaymentRate =
            simulationUiState.simulationParams.repaymentSpeed.toDoubleOrNull() ?: 0.0
        val interestRate = simulationUiState.simulationParams.interestRate.toDoubleOrNull() ?: 0.0
        var remainingDebt = debtorDebt.toDoubleOrNull() ?: 0.0
        var totalInterest = 0.0
        var elapsedSeconds = 1L

        while (remainingDebt > 0) {
            val actualPaymentRate = if (remainingDebt <= repaymentRate) {
                remainingDebt
            } else {
                repaymentRate
            }

            remainingDebt -= actualPaymentRate

            val decimalInterest = interestRate / 100

            totalInterest += remainingDebt * decimalInterest
            remainingDebt *= 1 + decimalInterest

            if (remainingDebt < 0) {
                remainingDebt = 0.0
            }

            delay(1000)

            elapsedSeconds++
            println("SIMULATE: $actualPaymentRate - $remainingDebt - $totalInterest - $elapsedSeconds")
        }

        updateResult(
            PaymentResults(
            totalInterestPaid = totalInterest,
            totalTime = elapsedSeconds
        )
        )


        stopSimulation()
    }

    fun updateResult(paymentResults: PaymentResults) {
        simulationUiState = simulationUiState.copy(
            paymentResults = paymentResults
        )
    }

    fun updateFlags(simulationFlags: SimulationFlags) {
        simulationUiState = simulationUiState.copy(
            simulationFlags = simulationFlags
        )
    }

    private fun startSimulation() {
        updateFlags(
            simulationUiState.simulationFlags.copy(
                isSimulationStarted = true,
                isSimulationFinished = false,
                isSimulationCancelled = false
            )
        )
    }

    private fun stopSimulation() {
        updateFlags(
            simulationUiState.simulationFlags.copy(
                isSimulationStarted = false,
                isSimulationFinished = true
            )
        )

    }

    fun cancelSimulation() {
        updateFlags(
            simulationUiState.simulationFlags.copy(
                isSimulationFinished = false,
                isSimulationStarted = false,
            )
        )
    }
}

data class SimulationUiState(
    val debtorDetails: DebtorDetails = DebtorDetails(),
    val simulationParams: SimulationParams = SimulationParams(),
    val paymentResults: PaymentResults = PaymentResults(),
    val simulationFlags: SimulationFlags = SimulationFlags()
)

data class SimulationFlags(
    val isSimulationStarted: Boolean = false,
    val isSimulationFinished: Boolean = false,
    val isSimulationCancelled: Boolean = false
)

data class SimulationParams(val interestRate: String = "0.0", val repaymentSpeed: String = "0.0")
data class PaymentResults(
    val totalInterestPaid: Double = 0.0,
    val totalTime: Long = 0L
)