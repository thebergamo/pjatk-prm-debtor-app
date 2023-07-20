package br.com.thedon.debtorapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.data.debtor.DebtorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View Model to retrieve all items in the Room database.
 */
class HomeViewModel(private val debtorsRepository: DebtorRepository) : ViewModel() {

    private val itemList = MutableStateFlow<List<Debtor>>(listOf())
    private val showDeleteDialog = MutableStateFlow(false)
    private val debtorIdToDelete = MutableStateFlow<Int?>(null)
    private val totalDebt = MutableStateFlow(0.0)

    private val _uiState = MutableStateFlow(HomeUiState())

    val homeUiState: StateFlow<HomeUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            combine(
                showDeleteDialog,
                debtorsRepository.getAllDebtorsStream(),
                debtorIdToDelete,
                debtorsRepository.debtSum(),
            ) { showDeleteDialog, debtorsList, debtorIdToDelete, sumDebt ->
                itemList.value = debtorsList
                totalDebt.value = sumDebt

                HomeUiState(debtorsList, showDeleteDialog, debtorIdToDelete, sumDebt)
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun showDeleteDebtor(id: Int) {
        showDeleteDialog.value = true
        debtorIdToDelete.value = id
    }

    fun hideDeleteDebtor() {
        showDeleteDialog.value = false
    }

    suspend fun deleteDebtor() {
        debtorsRepository.deleteDebtor(debtorIdToDelete.value!!)
        showDeleteDialog.value = false
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(
    val itemList: List<Debtor> = listOf(),
    val showDeleteDialog: Boolean = false,
    val debtorIdToDelete: Int? = null,
    val totalDebt: Double = 0.0
)