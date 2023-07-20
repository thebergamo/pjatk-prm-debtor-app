package br.com.thedon.debtorapp.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import br.com.thedon.debtorapp.data.AppDataContainer
import br.com.thedon.debtorapp.data.debtor.Debtor
import br.com.thedon.debtorapp.data.debtor.DebtorRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DebtorContentProvider : ContentProvider() {
    companion object {
        private const val AUTHORITTY = "br.com.thedon.debtorapp.provider"
        private const val TABLE = Debtor.TABLE_NAME
        private const val LIST_ACTION = 1
        private const val GET_ACTION = 2
    }

    private lateinit var debtorRepository: DebtorRepository

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITTY, TABLE, LIST_ACTION)
        addURI(AUTHORITTY, "$TABLE/#", GET_ACTION)
    }

    override fun onCreate(): Boolean {
        context?.let {
            debtorRepository = AppDataContainer(it).debtorsRepository
        }

        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("_id", "name", "debt"))

        when (uriMatcher.match(uri)) {
            LIST_ACTION -> {
                runBlocking {
                    debtorRepository.getAllDebtorsStream().first().forEach {
                        cursor.addRow(arrayOf(it.id, it.name, it.debt))
                    }
                }
            }

            GET_ACTION -> {
                val id = ContentUris.parseId(uri)
                runBlocking {
                    debtorRepository.getDebtorStream(id.toInt()).first()?.let {
                        cursor.addRow(arrayOf(it.id, it.name, it.debt))
                    }
                }
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            LIST_ACTION -> {
                val id = runBlocking {
                    if (values == null) {
                        throw IllegalArgumentException("Invalid values provided")
                    }

                    return@runBlocking debtorRepository.insertDebtor(fromContentValues(values))
                }

                context?.contentResolver?.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }

            GET_ACTION -> {
                throw IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            LIST_ACTION -> {
                throw IllegalArgumentException("Invalid URI, cannot update without ID" + uri); }

            GET_ACTION -> {
                val count = runBlocking {
                    if (values == null) {
                        throw IllegalArgumentException("Invalid values provided")
                    }

                    val contentDebtor = fromContentValues(values)
                    val debtor = Debtor(
                        id = ContentUris.parseId(uri).toInt(),
                        name = contentDebtor.name,
                        debt = contentDebtor.debt
                    )

                    return@runBlocking debtorRepository.updateDebtor(debtor)
                }

                context?.contentResolver?.notifyChange(uri, null)
                return count
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (uriMatcher.match(uri)) {
            LIST_ACTION -> {
                throw IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            }

            GET_ACTION -> {
                val count = runBlocking {
                    return@runBlocking debtorRepository.deleteDebtor(
                        ContentUris.parseId(uri).toInt()
                    )
                }

                context?.contentResolver?.notifyChange(uri, null)
                return count
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            LIST_ACTION -> "vnd.android.cursor.dir/${AUTHORITTY}.$TABLE"
            GET_ACTION -> "vnd.android.cursor.item/${AUTHORITTY}.$TABLE"
            else -> null
        }
    }

}

fun fromContentValues(contentValues: ContentValues): Debtor {
    val name = if (contentValues.containsKey("name")) contentValues.getAsString("name") else null
    val debt = if (contentValues.containsKey("debt")) contentValues.getAsDouble("debt") else null

    if (name == null || debt == null) {
        throw IllegalArgumentException("Content Values don't have a valid Debtor")
    }

    return Debtor(
        name = name,
        debt = debt
    )
}