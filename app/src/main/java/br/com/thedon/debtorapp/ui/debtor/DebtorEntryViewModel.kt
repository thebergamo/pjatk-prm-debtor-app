package br.com.thedon.debtorapp.ui.debtor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.data.debtor.DebtorRepository

/**
 * View Model to validate and insert debtors in the Room database.
 */
class DebtorEntryViewModel(private val debtorsRepository: DebtorRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var debtorUiState by mutableStateOf(DebtorUiState())
        private set

    /**
     * Updates the [debtorUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(debtorDetails: DebtorDetails) {
        debtorUiState =
            DebtorUiState(debtorDetails = debtorDetails, isEntryValid = validateInput(debtorDetails))
    }

    /**
     * Inserts an [Debtor] in the Room database
     */
    suspend fun saveDebtor() {
        if (validateInput()) {
            debtorsRepository.insertDebtor(debtorUiState.debtorDetails.toDebtor())
        }
    }

    private fun validateInput(uiState: DebtorDetails = debtorUiState.debtorDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && debt.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Debtor.
 */
data class DebtorUiState(
    val debtorDetails: DebtorDetails = DebtorDetails(),
    val isEntryValid: Boolean = false,
)

data class DebtorDetails(
    val id: Int = 0,
    val name: String = "",
    val debt: String = "",
)

/**
 * Extension function to convert [DebtorUiState] to [Debtor]. If the value of [DebtorDetails.debt] is
 * not a valid [Double], then the debt will be set to 0.0.
 */
fun DebtorDetails.toDebtor(): Debtor = Debtor(
    id = id,
    name = name,
    debt = debt.toDoubleOrNull() ?: 0.0,
)

/**
 * Extension function to convert [Debtor] to [DebtorUiState]
 */
fun Debtor.toDebtorUiState(isEntryValid: Boolean = false): DebtorUiState = DebtorUiState(
    debtorDetails = this.toDebtorDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Debtor] to [DebtorDetails]
 */
fun Debtor.toDebtorDetails(): DebtorDetails = DebtorDetails(
    id = id,
    name = name,
    debt = debt.toString(),
)