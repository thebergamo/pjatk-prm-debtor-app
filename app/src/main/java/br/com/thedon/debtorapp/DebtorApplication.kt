package br.com.thedon.debtorapp

import android.app.Application
import br.com.thedon.debtorapp.data.AppContainer
import br.com.thedon.debtorapp.data.AppDataContainer

class DebtorApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}