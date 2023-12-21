package com.lokis.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lokis.R

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_search)
        val etQuery = view.findViewById<TextView>(R.id.et_query)
        val rv_search = view.findViewById<RecyclerView>(R.id.rv_search)

        btnSearch.setOnClickListener {
            searchLocation()
        }

        etQuery.setOnKeyListener { _, keycode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER) {
                searchLocation()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        adapter = SearchAdapter()
        adapter.notifyDataSetChanged()
        rv_search.setHasFixedSize(true)
        rv_search.layoutManager = LinearLayoutManager(activity)
        rv_search.adapter = adapter
        searchViewModel.getSearchLocation().observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setList(it)
                progressbar(false)
            }
        }
    }

    private fun searchLocation() {
        val etQuerys = requireView().findViewById<TextView>(R.id.et_query)
        val query = etQuerys.text.toString()
        if (query.isEmpty()) return
        progressbar(true)
        searchViewModel.setSearchLocation(query)
    }

    private fun progressbar(state: Boolean) {
        val progressBar: ProgressBar = requireView().findViewById(R.id.progressBar)
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}