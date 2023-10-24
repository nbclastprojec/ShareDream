package com.dreamteam.sharedream.Util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class Util {

    fun showDialog(context: Context, setTitle: String, setMessage: String, completion: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle(setTitle)
            setMessage(setMessage)
            setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int -> completion() }
            setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            show()
        }
    }
}
