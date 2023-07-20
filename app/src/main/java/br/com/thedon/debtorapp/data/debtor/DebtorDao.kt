package br.com.thedon.debtorapp.data.debtor

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtorDao {
    @Query("SELECT * from debtors ORDER BY name ASC")
    fun getAllDebtors(): Flow<List<Debtor>>

    @Query("SELECT * from debtors WHERE id = :id")
    fun getDebtor(id: Int): Flow<Debtor>

    @Query("SELECT sum(debt) from debtors")
    fun debtSum(): Flow<Double>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Debtor into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(debtor: Debtor): Long

    @Update
    suspend fun update(debtor: Debtor): Int

    @Query("DELETE from debtors WHERE id = :debtorId")
    suspend fun delete(debtorId: Int): Int
}