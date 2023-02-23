package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentFavoriteWeatherListBinding
import com.esfimus.gbweather.domain.RecyclerAdapter
import com.esfimus.gbweather.domain.SharedViewModel
import com.esfimus.gbweather.domain.clicks.OnListItemCLick
import com.esfimus.gbweather.domain.clicks.OnListItemLongClick

class FavoriteWeatherListFragment : Fragment() {

    private var bindingNullable: FragmentFavoriteWeatherListBinding? = null
    private val binding get() = bindingNullable!!
    private lateinit var model: SharedViewModel

    companion object {
        fun newInstance() = FavoriteWeatherListFragment()
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
        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        context?.let { model.load(it) }
        model.weatherList.observe(viewLifecycleOwner) {
            val customAdapter = RecyclerAdapter(it)
            binding.weatherRecycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = customAdapter
            }
            // reaction on item click
            customAdapter.setListItemClickListener(object : OnListItemCLick {
                override fun onClick(position: Int) {
                    model.switchWeatherLocation(position)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
            // reaction on item long click
            customAdapter.setListItemLongClickListener(object : OnListItemLongClick{
                override fun onLongCLick(position: Int, itemView: View) {
                    popupMenu(position, itemView)
                }
            })
        }
        binding.addWeatherLocation.setOnClickListener {
            openFragment(AddWeatherLocationFragment.newInstance())
        }
    }

    private fun popupMenu(position: Int, itemView: View) {
        PopupMenu(context, itemView).apply {
            inflate(R.menu.popup_menu)
            show()
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_popup -> {
                        model.deleteWeatherLocation(position)
                        true
                    }
                    else -> false
                }
            }
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