package br.com.thedon.debtorapp.data.debtor

import kotlinx.coroutines.flow.Flow

interface DebtorRepository {
    /**
     * Retrieve all the debtors from the the given data source.
     */
    fun getAllDebtorsStream(): Flow<List<Debtor>>

    /**
     * Retrieve an debtor from the given data source that matches with the [id].
     */
    fun getDebtorStream(id: Int): Flow<Debtor?>

    /**
     * Retrieve sum of all debts from the given data source
     */
    fun debtSum(): Flow<Double>

    /**
     * Insert debtor in the data source
     */
    suspend fun insertDebtor(debtor: Debtor): Long

    /**
     * Delete debtor from the data source
     */
    suspend fun deleteDebtor(debtorId: Int): Int

    /**
     * Update debtor in the data source
     */
    suspend fun updateDebtor(debtor: Debtor): Int
}