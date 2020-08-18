package com.example.consumerapp

import android.view.View

class CustomOnclickListener(
    private val position: Int,
    private val onItemCallback: OnItemClickCallback
) : View.OnClickListener {

    override fun onClick(v: View) {
        onItemCallback.onItemClicked(v, position)
    }
    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }
}

