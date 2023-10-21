package com.dreamteam.sharedream.util

import android.util.Log
import android.view.View

class OnThrottleClickListener(
    private val onClickListener: View.OnClickListener,
    private val interval: Long = 300L
) : View.OnClickListener {

    private var clickable = true

    override fun onClick(v: View?) {
        if (clickable) {
            clickable = false
            v?.run {
                postDelayed({
                    clickable = true
                }, interval)
                onClickListener.onClick(v)
            }
        } else {
            Log.d("OnThrottleClickListener_onClick", "waiting for a while")
        }
    }

    fun View.onThrottleClick(action: (v: View) -> Unit) {
        val listener = View.OnClickListener { action(it) }
        setOnClickListener(OnThrottleClickListener(listener))
    }

    fun View.onThrottleClick(action: (v: View) -> Unit, interval: Long) {
        val listener = View.OnClickListener { action(it) }
        setOnClickListener(OnThrottleClickListener(listener, interval))
    }

}