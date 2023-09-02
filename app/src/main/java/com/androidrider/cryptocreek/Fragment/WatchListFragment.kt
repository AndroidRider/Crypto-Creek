package com.androidrider.cryptocreek.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.androidrider.cryptocreek.APIs.ApiInterface
import com.androidrider.cryptocreek.APIs.ApiUtilities
import com.androidrider.cryptocreek.Adapter.MarketAdapter
import com.androidrider.cryptocreek.Model.CryptoCurrency
import com.androidrider.cryptocreek.databinding.FragmentWatchListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchListFragment : Fragment() {

    private lateinit var binding: FragmentWatchListBinding
    private lateinit var watchList: ArrayList<String>
    private lateinit var watchListItem: ArrayList<CryptoCurrency>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentWatchListBinding.inflate(layoutInflater)

        lifecycleScope.launch(Dispatchers.IO){

            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()
            if (res.body() != null){
                withContext(Dispatchers.Main){

                    readData() // Call readData to populate watchListItem

                    // Populate watchListItem using the watchList
                    watchListItem = ArrayList()
                    if (res.body() != null) {
                        for (watchData in watchList) {
                            for (item in res.body()!!.data.cryptoCurrencyList) {
                                if (watchData == item.symbol) {
                                    watchListItem.add(item)
                                }
                            }
                        }
                    }

                    binding.spinKitView.visibility = GONE
                    binding.watchlistRecyclerView.adapter = MarketAdapter(requireContext(), watchListItem, "watchfragment")

                }
            }
        }
        return binding.root
    }

    private fun readData() {

        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("watchlist", ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json, type)
    }
}