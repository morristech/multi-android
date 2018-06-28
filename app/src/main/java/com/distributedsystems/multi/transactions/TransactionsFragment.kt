package com.distributedsystems.multi.transactions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getColor
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.common.toDp
import com.distributedsystems.multi.db.Wallet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.component_card.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionsFragment : Fragment() {

    companion object {
        fun newInstance() : TransactionsFragment = TransactionsFragment()
        private val LOG_TAG = TransactionsFragment::class.java.simpleName
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<TransactionsViewModel>
    private lateinit var viewModel : TransactionsViewModel
    private var wallet : Wallet? = null
    private var disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(TransactionsViewModel::class.java, viewModelFactory)

        transactions_list.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        renderWalletDetails()
        setOnAddressLongPress()
        setAppBarStateChangeListener()
    }


    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun setAppBarStateChangeListener() {
        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            Log.i(LOG_TAG, "Vertical Offset: $verticalOffset")
            val lp = list_holder.layoutParams as CoordinatorLayout.LayoutParams

            list_holder.layoutParams = lp.apply {
                    marginStart = toDp(Math.round(16 - (Math.abs(verticalOffset)/50).toFloat()))
                    marginEnd = toDp(Math.round((16 - Math.abs(verticalOffset)/50).toFloat()))
            }

            wallet_holder.rotationX = (verticalOffset/18f)

            if(Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                list_holder.setBackgroundColor(getColor(R.color.paleWhite))
            } else {
                list_holder.background = ContextCompat.getDrawable(context!!, R.drawable.transactions_list_holder_background)
            }
        }
    }

    private fun renderWalletDetails() {
        disposable.add(viewModel.getDefaultWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    wallet = it
                    val publicKey = wallet!!.ethAddress
                    val qrBitmap = QRCode.from(publicKey).bitmap()
                    issue_date.text = resources.getString(R.string.wallet_issue_date,
                            formatIssuedDate(wallet!!.insertedAt!!))
                    wallet_name.text = wallet!!.name
                    eth_address.text = resources.getString(R.string.wallet_address_subtext,
                            publicKey.substring(publicKey.length - 5 until publicKey.length))
                    qr_code.setImageBitmap(qrBitmap)
                    getTransactions()
                })
    }

    private fun getTransactions() {
        viewModel.getTransactions("0xEf13759c4Ae259aE9D17D43E65EF8c6C39035F24")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val walletTransactions = it.data()?.ethereumAddress()!!.transactions()
                    viewModel.setTransactionsForWallet(wallet!!, walletTransactions!!)

                    if(loading_spinner != null) loading_spinner.visibility = View.GONE

                    if(walletTransactions.isNotEmpty()) {
                        empty_list_label.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.VISIBLE
                        transactions_list.visibility = View.VISIBLE
                        transactions_list.adapter = TransactionsListAdapter(wallet!!, walletTransactions, context!!)
                    } else {
                        empty_list_label.visibility = View.VISIBLE
                        transactions_list.visibility = View.GONE
                    }
                }
    }

    private fun formatIssuedDate(date: Date) : String {
        val sdf = SimpleDateFormat("MM/YY", Locale.getDefault())
        return sdf.format(date)
    }

    private fun setOnAddressLongPress() {
        eth_address.setOnLongClickListener {
            val clip = ClipData.newPlainText("Ethereum Address", wallet?.ethAddress)
            val clipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = clip

            context?.toast("Copied Address to Clipboard")

            true
        }
    }
}