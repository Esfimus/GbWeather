package com.esfimus.gbweather.domain.clicks

import android.view.View

interface OnListItemLongClick {
    fun onLongCLick(position: Int, itemView: View)
}