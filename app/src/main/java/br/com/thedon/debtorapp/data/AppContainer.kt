package br.com.thedon.debtorapp.data

import android.content.Context
import br.com.thedon.debtorapp.data.debtor.DebtorRepository
import br.com.thedon.debtorapp.data.debtor.OfflineDebtorsRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val debtorsRepository: DebtorRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineDebtorsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [DebtorRepository]
     */
    override val debtorsRepository: DebtorRepository by lazy {
        OfflineDebtorsRepository(DebtorDatabase.getDatabase(context).debtorDao())
    }
}