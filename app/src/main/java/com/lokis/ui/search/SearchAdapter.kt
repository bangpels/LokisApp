package com.lokis.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lokis.R
import com.lokis.model.DataTravel

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewModel>() {

    private val list = ArrayList<DataTravel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(dataTravel: ArrayList<DataTravel>) {
        list.clear()
        list.addAll(dataTravel)
        notifyDataSetChanged()
    }

    inner class SearchViewModel(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(dataTravel: DataTravel) {
            val name = view.findViewById<TextView>(R.id.tvNameWisata)
            val city = view.findViewById<TextView>(R.id.tvCity)

            name.text = dataTravel.name
            city.text = dataTravel.city

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewModel {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item_two, parent, false)
        return SearchViewModel(v)
    }

    override fun onBindViewHolder(holder: SearchViewModel, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}