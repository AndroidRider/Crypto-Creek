package com.androidrider.cryptocreek.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.androidrider.cryptocreek.Fragment.HomeFragmentDirections
import com.androidrider.cryptocreek.Model.CryptoCurrency
import com.androidrider.cryptocreek.R
import com.androidrider.cryptocreek.databinding.TopCurrencyLayoutBinding
import com.bumptech.glide.Glide

class TopMarketAdapter(var context: Context, var list: List<CryptoCurrency>):
RecyclerView.Adapter<TopMarketAdapter.TopMarketviewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopMarketviewHolder {
        return TopMarketviewHolder(LayoutInflater.from(context).inflate(R.layout.top_currency_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TopMarketviewHolder, position: Int) {
        val item = list[position]

        holder.binding.topCurrencyNameTextView.text = item.name

        Glide.with(context).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/" + item.id + ".png"
        ).thumbnail(Glide.with(context).load(R.drawable.spinner))
            .into(holder.binding.topCurrencyImageView)


        if (item.quotes!![0].percentChange24h > 0){
            holder.binding.topCurrencyChangeTextView.setTextColor(context.resources.getColor(R.color.green))
            holder.binding.topCurrencyChangeTextView.text = "+${String.format("%.03f",item.quotes[0].percentChange24h)} %"
//            holder.binding.topCurrencyChangeTextView.text = "+${item.quotes[0].percentChange24h} %"// default
        }else{
            holder.binding.topCurrencyChangeTextView.setTextColor(context.resources.getColor(R.color.red))
            holder.binding.topCurrencyChangeTextView.text = "${String.format("%.03f",item.quotes[0].percentChange24h)} %"
        }

        holder.itemView.setOnClickListener {
            Navigation.findNavController(it).navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailsFragment(item))
        }
    }

    inner class TopMarketviewHolder(view : View):RecyclerView.ViewHolder(view){

        var binding = TopCurrencyLayoutBinding.bind(view)
    }
}