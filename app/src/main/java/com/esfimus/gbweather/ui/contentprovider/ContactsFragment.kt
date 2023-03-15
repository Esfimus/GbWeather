package com.esfimus.gbweather.ui.contentprovider

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentContactsBinding

const val REQUEST_CODE = 555

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
        checkPermission()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                        }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS) -> {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.contacts_access_title))
                        .setMessage(getString(R.string.contacts_access_message))
                        .setPositiveButton(getString(R.string.contacts_access_yes)) {
                                _, _ -> mRequestPermission() }
                        .setNegativeButton(getString(R.string.contacts_access_no)) { dialog, _ ->
                            dialog.cancel()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        .create()
                        .show()
                }
                else -> {
                    mRequestPermission()
                }
            }
        }
    }

    private fun getContacts() {
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver
            val contactsCursor: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            contactsCursor?.let { cursor ->
                for (i in 0 until cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val name = cursor.getString(columnIndex)
                        ui.contactsContainer.addView(AppCompatTextView(it).apply {
                            text = name
                            textSize = resources.getDimension(R.dimen.small_text_size)
                        })
                    }
                }
            }
            contactsCursor?.close()
        }
    }

    private fun mRequestPermission() {
        @Suppress("deprecation")
        requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts()
                } else {
                    context?.let {
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.contacts_access_title))
                            .setMessage(getString(R.string.contacts_access_message))
                            .setNegativeButton(getString(R.string.contacts_access_ok)) { dialog, _ ->
                                dialog.cancel()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                            .create()
                            .show()
                    }
                }
                return
            }
        }
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }
}