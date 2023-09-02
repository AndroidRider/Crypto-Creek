package com.androidrider.cryptocreek.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.cryptocreek.APIs.ApiInterface
import com.androidrider.cryptocreek.APIs.ApiUtilities
import com.androidrider.cryptocreek.Adapter.MarketAdapter
import com.androidrider.cryptocreek.Model.CryptoCurrency
import com.androidrider.cryptocreek.R
import com.androidrider.cryptocreek.databinding.FragmentDetailsBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding
    private val item: DetailsFragmentArgs by navArgs()

    lateinit var detailRecyclerView: RecyclerView
    private lateinit var list: List<CryptoCurrency>
    private lateinit var adapter: MarketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDetailsBinding.inflate(layoutInflater)
        detailRecyclerView = binding.detailRecyclerView
        val data : CryptoCurrency = item.data!!
        setupDetails(data)
        loadChart(data)
        setButtonOnClick(data)
        addToWatchList(data)
        loadData()
        return binding.root
    }

    var watchList: ArrayList<String>? = null
    var watchListIsChecked = false

    private fun addToWatchList(data: CryptoCurrency) {
        readData()
        watchListIsChecked = if (watchList!!.contains(data.symbol)){
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
            true
        }else{
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
            false
        }

        binding.addWatchlistButton.setOnClickListener {
            watchListIsChecked =
                if (!watchListIsChecked){
                    if (!watchList!!.contains(data.symbol)){
                        watchList!!.add(data.symbol)
                    }
                    storeData()
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star)
                    Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
                    true
                }else{
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline)
                    watchList!!.remove(data.symbol)
                    storeData()
                    Toast.makeText(requireContext(), "Removed", Toast.LENGTH_SHORT).show()
                    false
                }
        }
    }

    private fun storeData(){
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(watchList)
        editor.putString("watchlist", json)
        editor.apply()
    }

    private fun readData() {
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("watchlist", ArrayList<String>().toString())
        val type = object : TypeToken<ArrayList<String>>(){}.type
        watchList = gson.fromJson(json, type)
    }

    private fun setButtonOnClick(item: CryptoCurrency) {
        val fifteenMinute = binding.button5
        val oneHour = binding.button4
        val fourHour = binding.button3
        val oneDay = binding.button2
        val oneWeek = binding.button1
        val oneMonth = binding.button

        val clickListener = View.OnClickListener {
            when(it.id){
                fifteenMinute.id -> loadChartData(it, "15", item, oneHour, fourHour, oneDay, oneWeek, oneMonth )
                oneHour.id -> loadChartData(it, "1H", item, fifteenMinute, fourHour, oneDay, oneWeek, oneMonth )
                fourHour.id -> loadChartData(it, "4H", item, oneHour, fifteenMinute, oneDay, oneWeek, oneMonth )
                oneDay.id -> loadChartData(it, "D", item, oneHour, fourHour, fifteenMinute, oneWeek, oneMonth )
                oneWeek.id -> loadChartData(it, "W", item, oneHour, fourHour, oneDay, fifteenMinute, oneMonth )
                oneMonth.id -> loadChartData(it, "M", item, oneHour, fourHour, oneDay, oneWeek, fifteenMinute )
            }
        }
        fifteenMinute.setOnClickListener(clickListener)
        oneHour.setOnClickListener(clickListener)
        fourHour.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)

    }
    private fun loadChartData(
        it: View?,
        s: String,
        item: CryptoCurrency,
        oneHour: AppCompatButton,
        fourHour: AppCompatButton,
        oneDay: AppCompatButton,
        oneWeek: AppCompatButton,
        oneMonth: AppCompatButton
    )
    {
        disableButton(oneHour, fourHour, oneDay, oneWeek, oneMonth)
        it!!.setBackgroundResource(R.drawable.active_button)

        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.detaillChartWebView.loadUrl(
            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval="+s+"&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=" +
                    "[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }
    private fun disableButton(oneHour: AppCompatButton, fourHour: AppCompatButton, oneDay: AppCompatButton, oneWeek: AppCompatButton, oneMonth: AppCompatButton) {
        oneHour.background = null
        fourHour.background = null
        oneDay.background = null
        oneWeek.background = null
        oneMonth.background = null
    }

    private fun loadChart(item: CryptoCurrency) {
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.detaillChartWebView.loadUrl(
            "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + item.symbol
                .toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=" +
                    "[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }

    private fun setupDetails(data: CryptoCurrency) {
        binding.detailSymbolTextView.text = data.symbol
        Glide.with(requireContext()).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/" + data.id + ".png"
        ).thumbnail(Glide.with(requireContext()).load(R.drawable.spinner))
            .into(binding.detailImageView)
        binding.detailPriceTextView.text = "$ ${String.format("%.04f",data.quotes[0].price)} %"

        if (data.quotes!![0].percentChange24h > 0){
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green))
            binding.detailChangeTextView.text = "+${String.format("%.03f",data.quotes[0].percentChange24h)} %"
        }else{
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            binding.detailChangeTextView.text = "${String.format("%.03f",data.quotes[0].percentChange24h)} %"
        }
    }

    private fun loadData(){
        list = listOf()
        adapter = MarketAdapter(requireContext(), list, "details")
        binding.detailRecyclerView.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()
            if (res.body() != null) {
                withContext(Dispatchers.Main) {
                    list = res.body()!!.data.cryptoCurrencyList
                    adapter.updateData(list)
                    binding.spinKitView.visibility = View.GONE
                }
            }
        }
    }


}