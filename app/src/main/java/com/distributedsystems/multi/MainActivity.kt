package com.distributedsystems.multi

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var currentFragment : Int = R.id.profile
    private var disposable = CompositeDisposable()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if(item.itemId != currentFragment) {
            currentFragment = item.itemId
            when (item.itemId) {
                R.id.browse -> {
                    viewpager.setCurrentItem(0, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tokens -> {
                    viewpager.setCurrentItem(0, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.transactions -> {
                    viewpager.setCurrentItem(2, true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    viewpager.setCurrentItem(3, true)
                    return@OnNavigationItemSelectedListener true
                }
            }
        }
        false
    }

    fun getDisposable() : CompositeDisposable {
        return disposable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disableViewPagerSwiping()

        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        viewpager.adapter = viewPagerAdapter

        if(savedInstanceState == null) {
            viewpager.setCurrentItem(3, false)
            navigation.selectedItemId = R.id.profile
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun disableViewPagerSwiping() {
        viewpager.setOnTouchListener { _, _ -> true }
    }
}
