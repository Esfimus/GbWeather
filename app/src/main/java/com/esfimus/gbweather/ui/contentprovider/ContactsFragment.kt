package com.esfimus.gbweather.ui.contentprovider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esfimus.gbweather.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _ui: FragmentContactsBinding? = null
    private val ui get() = _ui!!

    companion object {
        fun newInstance() = ContactsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _ui = FragmentContactsBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }
}