package com.distributedsystems.multi.profile

import android.arch.lifecycle.ViewModel
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import io.reactivex.Flowable
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
        private val walletDb : WalletDao
) : ViewModel() {

    companion object {
        val LOG_TAG = ProfileViewModel::class.java.simpleName
    }

    fun getWallets() : Flowable<List<Wallet>> {
        return walletDb.getAll()
    }

    fun getDefaultWallet() : Flowable<Wallet> {
        val defaultWalletId = MultiApp.get().defaultSharedPreferences
                .getLong(Preferences.PREF_DEFAULT_WALLET_ID, 0)
        return walletDb.getWallet(defaultWalletId)
    }

}