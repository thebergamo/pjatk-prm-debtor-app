package br.com.thedon.debtorapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.thedon.debtorapp.DebtorApplication
import br.com.thedon.debtorapp.ui.debtor.DebtorEditViewModel
import br.com.thedon.debtorapp.ui.debtor.DebtorEntryViewModel
import br.com.thedon.debtorapp.ui.home.HomeViewModel
import br.com.thedon.debtorapp.ui.simulation.SimulationViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Debtor app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SimulationViewModel(
                this.createSavedStateHandle(),
            )
        }

        initializer {
            DebtorEditViewModel(
                this.createSavedStateHandle(),
                debtorApplication().container.debtorsRepository
            )
        }

        initializer {
            DebtorEntryViewModel(debtorApplication().container.debtorsRepository)
        }

        initializer {
            HomeViewModel(debtorApplication().container.debtorsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [DebtorApplication].
 */
fun CreationExtras.debtorApplication(): DebtorApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as DebtorApplication)