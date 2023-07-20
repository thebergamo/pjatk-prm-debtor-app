package br.com.thedon.debtorapp.data.debtor

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Debtor.TABLE_NAME)
data class Debtor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val debt: Double
) {
    companion object {
        const val TABLE_NAME = "debtors"
    }
}
