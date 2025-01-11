package com.madinaappstudio.viruscheck.tabs

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.madinaappstudio.viruscheck.api.RetrofitService
import com.madinaappstudio.viruscheck.databinding.FragmentFileScanBinding
import com.madinaappstudio.viruscheck.databinding.FragmentUrlScanBinding
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import com.madinaappstudio.viruscheck.utils.LoadingDialog
import com.madinaappstudio.viruscheck.utils.setLog
import com.madinaappstudio.viruscheck.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.coroutineContext

class UrlScanFragment : Fragment() {

    private var _binding: FragmentUrlScanBinding? = null
    private val binding get() = _binding!!
    private var launcher: ActivityResultLauncher<String>? = null
    private lateinit var dialog: LoadingDialog
    private val api = "ceabc53aeac2046b22be8f072449f6bf3f576c57405705d95d9af42dfe254852"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
            if (uri != null){
                dialog.show()
                dialog.setText("Uploading file...", "This won't take long!")
                scanFile(uri.toFile())
            } else {
                showToast(requireContext(), "Operation cancelled by user")
            }
        }
        _binding = FragmentUrlScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUploadUi()
        dialog = LoadingDialog(requireContext())

        binding.btnUrlScan.setOnClickListener {
            launcher?.launch("*/*")
        }

    }

    private fun scanFile(file: File){
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val call = RetrofitService.service.uploadFile(multipartBody, api)

        call.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                p0: Call<FileUploadResponse>, p1: Response<FileUploadResponse>
            ) {
                val response = p1.body()
                if (response != null) {
                    dialog.setText("Analyzing file...", "This may take long, please wait...")
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        getScanResult(getSHA256(file))
                    },1000)

                } else {
                    showUploadUi()
                    dialog.hide()
                    showToast(requireContext(), "Failed to upload file")
                }
            }

            override fun onFailure(p0: Call<FileUploadResponse>, p1: Throwable) {
                showUploadUi()
                dialog.hide()
                showToast(requireContext(), p1.localizedMessage)
            }

        })
    }

    private fun getScanResult(sha256: String){
        setLog(sha256)
        bindViewsData(null,sha256)
    }

    private fun bindViewsData(response: FileReportResponse?, sha256: String){
        dialog.hide()
        showResultUi()
    }

    private fun Uri.toFile(): File {
        val tempFile = File(requireActivity().cacheDir, getFileName())

        requireActivity().contentResolver.openInputStream(this)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun Uri.getFileName(): String {
        var name = ""
        if (scheme == "content"){
            val cursor = requireActivity().contentResolver.query(this, null, null, null)
            cursor?.use {
                if (it.moveToFirst()){
                    name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        return name.ifEmpty { lastPathSegment ?: "${System.currentTimeMillis()}" }
    }

    private fun formatDate(second: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy").withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochSecond(second))
    }

    private fun convertToSize(byte: Long): String {
        return when {
            byte <= 1024.0 -> {
                "$byte KB"
            }

            byte in 1024..1048576 -> {
                String.format(Locale.getDefault(), "%.2f KB", byte / 1024.0)
            }

            else -> {
                String.format(Locale.getDefault(), "%.2f MB", byte / (1024.0 * 1024.0))
            }
        }
    }

    fun getSHA256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { inputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }


    private fun showUploadUi(){
        binding.tv1UrlScan.visibility = View.VISIBLE
        binding.tv2UrlScan.visibility = View.GONE
    }

    private fun showResultUi(){
        binding.tv1UrlScan.visibility = View.GONE
        binding.tv2UrlScan.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}