package br.com.thedon.debtorapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.data.debtor.DebtorDao

@Database(entities = [Debtor::class], version = 1, exportSchema = false)
abstract class DebtorDatabase : RoomDatabase() {

    abstract fun debtorDao(): DebtorDao

    companion object {
        @Volatile
        private var Instance: DebtorDatabase? = null

        fun getDatabase(context: Context): DebtorDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DebtorDatabase::class.java, "debtors_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}