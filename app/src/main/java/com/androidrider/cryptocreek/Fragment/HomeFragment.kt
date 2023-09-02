package com.androidrider.cryptocreek.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.androidrider.cryptocreek.APIs.ApiInterface
import com.androidrider.cryptocreek.APIs.ApiUtilities
import com.androidrider.cryptocreek.Adapter.BannerAdapter
import com.androidrider.cryptocreek.Adapter.TopLossGainPagerAdapter
import com.androidrider.cryptocreek.Adapter.TopMarketAdapter
import com.androidrider.cryptocreek.R
import com.androidrider.cryptocreek.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private lateinit var bannerViewPager: ViewPager2 // banner slide
    private lateinit var bannerAdapter: BannerAdapter // banner slide
    private val imageResources = intArrayOf(
        R.drawable.banner, R.drawable.banner1, R.drawable.banner2,
        R.drawable.banner3, R.drawable.banner4, R.drawable.banner5)
    // Auto-slide related variables
    private val handler = Handler(Looper.getMainLooper())
    private val delay = 3000 // Delay in milliseconds between auto-slides


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(layoutInflater)


        init() // banner slide

        getTopCurrencyList()

        setTablayout()

        return binding.root
    }

    private fun setTablayout() {
        val adapter = TopLossGainPagerAdapter(this)
        binding.contentViewPager.adapter = adapter
        binding.contentViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0){
                    binding.topGainIndicator.visibility = VISIBLE
                    binding.topLoseIndicator.visibility = GONE
                }else{
                    binding.topGainIndicator.visibility = GONE
                    binding.topLoseIndicator.visibility = VISIBLE
                }
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.contentViewPager){tab, position ->
            var title = if (position == 0){
                "Top Gainers"
            }else{
                "Top Losers"
            }
            tab.text = title
        }.attach()
    }

    private fun getTopCurrencyList() {
        lifecycleScope.launch(Dispatchers.IO){
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            withContext(Dispatchers.Main){
                binding.topCurrencyRecyclerView.adapter = TopMarketAdapter(requireContext(), res.body()!!.data.cryptoCurrencyList)

            }
            Log.d("SHUBH","getTopCurrencyList: ${res.body()!!.data.cryptoCurrencyList}")
        }
    }


    // banner slide
    fun  init() {
        bannerViewPager = binding.bannerViewPager // banner slide
        bannerAdapter = BannerAdapter(imageResources) // banner slide
        bannerViewPager.adapter = bannerAdapter // banner slide
        // Start auto-slide when the fragment is created
        startAutoSlide() // banner slide
    }

    // banner slide
    override fun onDestroyView() {
        super.onDestroyView()
        // Stop the auto-slide handler when the fragment is destroyed
        stopAutoSlide()
    }

    private fun startAutoSlide() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = bannerViewPager.currentItem
                val nextItem = (currentItem + 1) % imageResources.size
                bannerViewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delay.toLong())
            }
        }
        handler.postDelayed(runnable, delay.toLong())
    }

    private fun stopAutoSlide() {
        handler.removeCallbacksAndMessages(null)
    }


}