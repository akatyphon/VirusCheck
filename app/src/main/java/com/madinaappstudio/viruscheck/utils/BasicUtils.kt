package com.madinaappstudio.viruscheck.utils

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.madinaappstudio.viruscheck.BuildConfig
import com.madinaappstudio.viruscheck.R

const val USER_NODE = "USERS"
const val DATABASE_NAME = "AppDatabase"

fun getVirusApi(): String {
    return BuildConfig.API_KEY
}

fun showToast(context: Context, msg: String?, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, "${msg}", length).show()
}

fun setLog(msg: Any?) {
    Log.d("VirusCheck-Log", "setLog: ${msg}")
}

fun generateUUID(): String {
    return "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10
}

class ProgressLoading(context: Context, dimAmount: Float = .7f) {
    private val dialogView =
        LayoutInflater.from(context).inflate(R.layout.dialog_progress_loading, null)
    private val builder = AlertDialog.Builder(context).create().apply {
        setView(dialogView)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(dimAmount)
    }

    val width = (context.resources.displayMetrics.widthPixels * 0.50)
    fun show() {
        builder.show()
        builder.window?.setLayout(
            width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun hide() {
        builder.hide()
    }
}


