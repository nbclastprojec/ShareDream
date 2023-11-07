package com.dreamteam.sharedream.Util

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.view.MapViewFragment

object Util {

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

    val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun permissionCheck(context: Context): Boolean {

        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun hideKeypad(context: Context, view: View) {
        // 키보드 입력창 내리기
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}


object ToastMsg{
    var toast: Toast? = null
    fun makeToast(context: Context,message: String) {
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}
