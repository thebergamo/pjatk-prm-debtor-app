package br.com.thedon.debtorapp.ui.debtor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.data.debtor.DebtorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [DebtorRepository]'s data source.
 */
class DebtorEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val debtorsRepository: DebtorRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var debtorUiState by mutableStateOf(DebtorUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[DebtorEditDestination.itemIdArg])

    private var savedDebtor: Debtor? = null

    var confirmDialog by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val debtor = debtorsRepository.getDebtorStream(itemId)
                .filterNotNull()
                .first()

            savedDebtor = debtor
            debtorUiState = debtor.toDebtorUiState(true)
        }
    }

    /**
     * Update the item in the [DebtorRepository]'s data source
     */
    suspend fun updateDebtor() {
        if (validateInput(debtorUiState.debtorDetails)) {
            debtorsRepository.updateDebtor(debtorUiState.debtorDetails.toDebtor())
            confirmDialog = false;
        }
    }

    fun isDebtorModified(): Boolean {
       return  debtorUiState.debtorDetails.name != savedDebtor?.name || debtorUiState.debtorDetails.debt.equals(savedDebtor?.debt)
    }

    /**
     * Updates the [debtorUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(debtorDetails: DebtorDetails) {
        debtorUiState =
            DebtorUiState(debtorDetails = debtorDetails, isEntryValid = validateInput(debtorDetails))
    }

    fun showChangesDialog() {
        confirmDialog = true;
    }

    fun hideChangesDialog() {
        confirmDialog = false
    }

    private fun validateInput(uiState: DebtorDetails = debtorUiState.debtorDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && debt.isNotBlank()
        }
    }
}