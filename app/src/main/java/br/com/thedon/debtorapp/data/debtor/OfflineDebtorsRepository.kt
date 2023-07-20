package br.com.thedon.debtorapp.data.debtor

import kotlinx.coroutines.flow.Flow

class OfflineDebtorsRepository(private val debtorDao: DebtorDao) : DebtorRepository {
    override fun getAllDebtorsStream(): Flow<List<Debtor>> = debtorDao.getAllDebtors()

    override fun getDebtorStream(id: Int): Flow<Debtor?> = debtorDao.getDebtor(id)

    override fun debtSum(): Flow<Double> = debtorDao.debtSum()

    override suspend fun insertDebtor(debtor: Debtor): Long = debtorDao.insert(debtor)

    override suspend fun deleteDebtor(debtorId: Int): Int = debtorDao.delete(debtorId)

    override suspend fun updateDebtor(debtor: Debtor): Int = debtorDao.update(debtor)
}