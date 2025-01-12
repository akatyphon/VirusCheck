package com.madinaappstudio.viruscheck.utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textview.MaterialTextView
import com.madinaappstudio.viruscheck.BuildConfig
import com.madinaappstudio.viruscheck.R

fun getVirusApi(): String {
    return BuildConfig.API_KEY
}

fun showToast(context: Context, msg: String?, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, "${msg}", length).show()
}

fun setLog(msg: Any?) {
    Log.d("VirusCheck-Log", "setLog: ${msg}")
}

class LoadingDialog(private val context: Context) {

    private val dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_loading, null)

    private val builder = AlertDialog.Builder(context).create().apply {
        setView(dialogView)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(1f)
    }

    fun show() {
        builder.show()
        builder.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.80).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun hide() {
        if (builder.isShowing) builder.hide()
    }

    fun setText(status: String, msg: String) {
        val tvStatus = dialogView.findViewById<MaterialTextView>(R.id.tvDialogStatus)
        val tvMsg = dialogView.findViewById<MaterialTextView>(R.id.tvDialogMsg)

        tvStatus.text = status
        tvMsg.text = msg
    }

    fun makeSuccessView(isFile: Boolean) {
        val pbCircular = dialogView.findViewById<ProgressBar>(R.id.pbDialogProgressC)
        val ivSuccess = dialogView.findViewById<ImageView>(R.id.ivDialogSuccess)
        val tvStatus = dialogView.findViewById<MaterialTextView>(R.id.tvDialogStatus)
        val pbHorizontal = dialogView.findViewById<ProgressBar>(R.id.pbDialogProgressH)
        val tvMsg = dialogView.findViewById<MaterialTextView>(R.id.tvDialogMsg)

        pbCircular.visibility = View.GONE
        ivSuccess.visibility = View.VISIBLE
        tvStatus.text = "Scan Completed"
        tvStatus.setTypeface(null, Typeface.BOLD)
        pbHorizontal.visibility = View.GONE
        if (isFile){
            tvMsg.text = "File have been successfully analyzed"
        } else {
            tvMsg.text = "URL have been successfully analyzed"
        }
    }
}


