package com.distributedsystems.multi.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.db.Wallet
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<ProfileViewModel>
    private lateinit var viewModel : ProfileViewModel

    companion object {
        fun newInstance() : ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(ProfileViewModel::class.java, viewModelFactory)

    }

}