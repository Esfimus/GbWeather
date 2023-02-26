package com.esfimus.gbweather.ui.favorite

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
import com.esfimus.gbweather.ui.SharedViewModel
import com.esfimus.gbweather.ui.favorite.clicks.OnListItemCLick
import com.esfimus.gbweather.ui.favorite.clicks.OnListItemLongClick
import com.esfimus.gbweather.ui.add.AddWeatherLocationFragment

class FavoriteWeatherListFragment : Fragment() {

    private var _ui: FragmentFavoriteWeatherListBinding? = null
    private val ui get() = _ui!!
    private val model: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java] }

    companion object {
        fun newInstance() = FavoriteWeatherListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _ui = FragmentFavoriteWeatherListBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDynamicList()
    }

    private fun initDynamicList() {
        context?.let { model.load(it) }
        model.weatherList.observe(viewLifecycleOwner) {
            val customAdapter = RecyclerAdapter(it)
            ui.weatherRecycler.apply {
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
            customAdapter.setListItemLongClickListener(object : OnListItemLongClick {
                override fun onLongCLick(position: Int, itemView: View) {
                    popupMenu(position, itemView)
                }
            })
        }
        ui.addWeatherLocation.setOnClickListener {
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

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }
}