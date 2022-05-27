package com.example.fireapplicatioin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fireapplicatioin.adapter.ItemAdapter
import com.example.fireapplicatioin.databinding.FragmentNewsBinding
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentNewsBinding>(inflater, R.layout.fragment_news, container, false)
        val recyclerView = binding.recyclerView
        recyclerView.adapter = ItemAdapter(context!!)
        val llm = LinearLayoutManager(context!!)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.addOnScrollListener(OnScrollListener(activity!!.findViewById(R.id.bottom_navigation)))
        return binding.root
    }
}

class OnScrollListener(val bottom_navigation: BottomNavigationView): RecyclerView.OnScrollListener(){

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0 && bottom_navigation.isShown) {
            bottom_navigation.visibility = View.GONE
        } else if (dy < 0) {
            bottom_navigation.visibility = View.VISIBLE
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }
}