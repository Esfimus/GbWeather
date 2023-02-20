package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentFavoriteWeatherListBinding
import com.esfimus.gbweather.domain.RecyclerAdapter
import com.esfimus.gbweather.domain.SharedViewModel
import com.esfimus.gbweather.domain.clicks.OnListItemCLick

class FavoriteWeatherListFragment : Fragment() {

    private var bindingNullable: FragmentFavoriteWeatherListBinding? = null
    private val binding get() = bindingNullable!!

    companion object {
        fun newInstance() = FavoriteWeatherListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        bindingNullable = FragmentFavoriteWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDynamicList()
    }

    private fun initDynamicList() {
        val model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        context?.let { model.load(it) }
        model.weatherList.observe(viewLifecycleOwner) {
            val recyclerView: RecyclerView = binding.weatherRecycler
            val customAdapter = RecyclerAdapter(it)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = customAdapter
            // reaction on item click
            customAdapter.setListItemClickListener(object : OnListItemCLick {
                override fun onClick(position: Int) {
                    model.switchWeatherLocation(position)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
        }
        binding.addWeatherLocation.setOnClickListener {
            openFragment(AddWeatherLocationFragment.newInstance())
        }
    }

    private fun openFragment(fragment: Fragment) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingNullable = null
    }

}