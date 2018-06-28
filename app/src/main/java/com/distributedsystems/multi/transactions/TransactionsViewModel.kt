package com.distributedsystems.multi.transactions

import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import com.distributedsystems.multi.type.ETHEREUM_NETWORK
import io.reactivex.Flowable
import io.reactivex.Observable
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
        private val apolloClient: ApolloClient,
        private val walletDb: WalletDao
) : ViewModel() {

    private var transactionsModel: TransactionsModel = TransactionsModel()

    fun getTransactions(hash: String): Observable<Response<TransactionFeedQuery.Data>> {
        val transactionFeedQuery = TransactionFeedQuery.builder()
                .address(EthereumAddressString(hash))
                .network(ETHEREUM_NETWORK.ROPSTEN)
                .build()

        val apolloCall = apolloClient.query(transactionFeedQuery)
        return Rx2Apollo.from(apolloCall)
    }

    fun getWallets() : Flowable<List<Wallet>> {
        return walletDb.getAll()
    }

    fun getDefaultWallet() : Flowable<Wallet> {
        val defaultWalletId = MultiApp.get().defaultSharedPreferences
                .getLong(Preferences.PREF_DEFAULT_WALLET_ID, 0)
        return walletDb.getWallet(defaultWalletId)
    }

    fun setTransactionsForWallet(wallet: Wallet, transactions: List<TransactionFeedQuery.Transaction>) {
        transactionsModel.transactions!!.plus(Pair(wallet.id, transactions))
    }
}