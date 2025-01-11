package com.madinaappstudio.viruscheck.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textview.MaterialTextView
import com.madinaappstudio.viruscheck.BuildConfig
import com.madinaappstudio.viruscheck.R

fun getVirusApi() : String {
    return BuildConfig.API_KEY
}

fun showToast(context: Context, msg: String?, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, "${msg}", length).show()
}

fun setLog(msg: Any?) {
    Log.d("VirusCheck-Log", "setLog: ${msg}")
}

class LoadingDialog(context: Context){

    private val dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_loading, null)

    private val builder = AlertDialog.Builder(context).create().apply {
        setView(dialogView)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(.9f)
    }

    fun show(){
        builder.show()
    }

    fun hide(){
        if (builder.isShowing) builder.hide()
    }

    fun setText(status: String, msg: String){
        val tvStatus = dialogView.findViewById<MaterialTextView>(R.id.tvDialogStatus)
        val tvMsg = dialogView.findViewById<MaterialTextView>(R.id.tvDialogMsg)

        tvStatus.text = status
        tvMsg.text = msg
    }

}


