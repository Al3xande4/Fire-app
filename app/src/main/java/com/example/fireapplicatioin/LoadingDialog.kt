package com.example.fireapplicatioin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog


class LoadingDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null
    @SuppressLint("InflateParams")
    fun startLoading() {
        val builder = AlertDialog.Builder(activity, R.style.loadingDialogStyle)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_layout, null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    fun stopLoading() {
        alertDialog!!.dismiss()
    }
}