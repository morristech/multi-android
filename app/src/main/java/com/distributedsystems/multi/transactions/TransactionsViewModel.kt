package com.distributedsystems.multi.transactions

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import com.distributedsystems.multi.type.ETHEREUM_NETWORK
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
        private val apolloClient: ApolloClient,
        private val walletDb: WalletDao
) : ViewModel() {

    companion object {
        private val LOG_TAG = TransactionsViewModel::class.java.simpleName
    }

    val transactions = MutableLiveData<List<TransactionFeedQuery.Transaction>>()
    val wallet = MutableLiveData<Wallet>()

    fun getTransactions() {//: Observable<Response<TransactionFeedQuery.Data>> {
        val hash = wallet.value?.ethAddress!!
        val transactionFeedQuery = TransactionFeedQuery.builder()
                .address(EthereumAddressString(hash))
                .network(ETHEREUM_NETWORK.ROPSTEN)
                .build()

        val apolloCall = apolloClient.query(transactionFeedQuery)
        Rx2Apollo.from(apolloCall)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    transactions.postValue(it.data()!!.ethereumAddress()!!.transactions())
                }, {
                    Log.e(LOG_TAG, it.localizedMessage)
                    MultiApp.get().toast("Unable to load transactions")
                })
    }

    fun getDefaultWallet() {
        val defaultWalletId = MultiApp.get().defaultSharedPreferences
                .getLong(Preferences.PREF_DEFAULT_WALLET_ID, 0)
        walletDb.getWallet(defaultWalletId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    wallet.postValue(it)
                }, {
                    MultiApp.get().toast("Unable to load wallet")
                })
    }
}