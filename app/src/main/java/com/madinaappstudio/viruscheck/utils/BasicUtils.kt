package com.madinaappstudio.viruscheck.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.madinaappstudio.viruscheck.BuildConfig
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.databinding.DialogScanLoadingBinding

const val USER_NODE = "USERS"

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

    private val binding = DialogScanLoadingBinding.inflate(LayoutInflater.from(context))

    private val builder = AlertDialog.Builder(context).create().apply {
        setView(binding.root)
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
        binding.tvDialogStatus.text = status
        binding.tvDialogMsg.text = msg
    }

    fun makeSuccessView(isFile: Boolean) {
        binding.pbDialogProgressC.visibility = View.GONE
        binding.ivDialogSuccess.visibility = View.VISIBLE
        binding.tvDialogStatus.text = "Scan Completed"
        binding.tvDialogStatus.setTypeface(null, Typeface.BOLD)
        binding.pbDialogProgressH.visibility = View.GONE
        if (isFile){
            binding.tvDialogMsg.text = "File have been successfully analyzed"
        } else {
            binding.tvDialogMsg.text = "URL have been successfully analyzed"
        }
    }

}

fun generateUUID(): String {
    return "35" +
            Build.BOARD.length % 10 +
            Build.BRAND.length % 10 +
            Build.DEVICE.length % 10 +
            Build.DISPLAY.length % 10 +
            Build.HOST.length % 10 +
            Build.ID.length % 10 +
            Build.MANUFACTURER.length % 10 +
            Build.MODEL.length % 10 +
            Build.PRODUCT.length % 10 +
            Build.TAGS.length % 10 +
            Build.TYPE.length % 10 +
            Build.USER.length % 10
}

class ProgressLoading(context: Context) {
    private val dialogView = LayoutInflater.from(context)
        .inflate(R.layout.dialog_progress_loading, null)
    private val builder = AlertDialog.Builder(context).create().apply {
        setView(dialogView)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(.7f)
    }

    val width = (context.resources.displayMetrics.widthPixels * 0.50)
    fun show(){
        builder.show()
        builder.window?.setLayout(
            width.toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun hide(){
        builder.hide()
    }
}


