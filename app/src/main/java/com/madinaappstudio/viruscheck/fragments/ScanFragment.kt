package com.madinaappstudio.viruscheck.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.api.RetrofitService
import com.madinaappstudio.viruscheck.databinding.FragmentScanBinding
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import com.madinaappstudio.viruscheck.models.ScanResultType
import com.madinaappstudio.viruscheck.models.UrlScanReportResponse
import com.madinaappstudio.viruscheck.models.UrlScanResponse
import com.madinaappstudio.viruscheck.utils.LoadingDialog
import com.madinaappstudio.viruscheck.utils.getVirusApi
import com.madinaappstudio.viruscheck.utils.setLog
import com.madinaappstudio.viruscheck.utils.showToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var launcher: ActivityResultLauncher<String>? = null
    private lateinit var loadingDialog: LoadingDialog
    private val apiKey = getVirusApi()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                loadingDialog.show()
                loadingDialog.setText("Uploading file...", "This won't take long!")
                scanFile(uri.toFile())
            } else {
                showToast(requireContext(), "Operation cancelled by user")
            }
        }
        _binding = FragmentScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        binding.btnScanFileUpload.setOnClickListener {
            launcher?.launch("*/*")
        }

        binding.btnScanUrl.setOnClickListener {
            val url = binding.etScanInputUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                if (isValidUrl(url)) scanUrl(url) else showToast(requireContext(), "Invalid url")
            } else {
                showToast(requireContext(), "Please enter url")
            }
        }

        binding.mtScanToolbar.setOnMenuItemClickListener { menuItem ->
            val itemId = menuItem.itemId
            if (itemId == R.id.btnScanMenuHistory) {
                showToast(requireContext(), "work in progress")
            }
            true
        }

    }

    private fun isValidUrl(url: String): Boolean {
        val domainRegex = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return domainRegex.matches(url)
    }

    private fun scanFile(file: File) {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val call = RetrofitService.service.uploadFile(multipartBody, apiKey)

        call.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                p0: Call<FileUploadResponse>, p1: Response<FileUploadResponse>
            ) {
                val response = p1.body()
                if (response != null) {
                    loadingDialog.setText("Analyzing file...", "This may take 40 - 45 sec, please wait...")
                    val sha256 = getSHA256(file)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        getFileReport(sha256)
                    }, 50000)

                } else {
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<FileUploadResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun getFileReport(sha256: String) {

        val call = RetrofitService.service.getFileReport(sha256, apiKey)

        call.enqueue(object : Callback<FileReportResponse> {
            override fun onResponse(
                p0: Call<FileReportResponse>,
                p1: Response<FileReportResponse>
            ) {
                val response = p1.body()
                if (response != null) {
                    loadingDialog.makeSuccessView(true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.hide()
                        val action = ScanFragmentDirections.actionScanToScanResult(
                            ScanResultType(fileReportResponse = response)
                        )
                        findNavController().navigate(action)
                    }, 1500)

                } else {
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<FileReportResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun scanUrl(url: String) {

        loadingDialog.show()
        loadingDialog.setText("Analyzing url...", "This may take long, please wait...")

        val encodedUrl = "url=${URLEncoder.encode(url, "UTF-8")}"
        val requestBody =
            encodedUrl.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val call = RetrofitService.service.scanUrl(requestBody, apiKey)

        call.enqueue(object : Callback<UrlScanResponse> {
            override fun onResponse(p0: Call<UrlScanResponse>, p1: Response<UrlScanResponse>) {
                val body = p1.body()
                if (body != null) {
                    val handler = Handler(Looper.getMainLooper())
                    val urlBase64 = getBase64(url)
                    handler.postDelayed({
                        getUrlReport(urlBase64)
                    }, 5000)
                } else {
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<UrlScanResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun getUrlReport(analysisId: String) {

        val call = RetrofitService.service.getUrlReport(analysisId, apiKey)

        call.enqueue(object : Callback<UrlScanReportResponse> {
            override fun onResponse(
                p0: Call<UrlScanReportResponse>,
                p1: Response<UrlScanReportResponse>
            ) {
                val body = p1.body()
                if (body != null) {
                    loadingDialog.makeSuccessView(false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingDialog.hide()
                        val action = ScanFragmentDirections.actionScanToScanResult(
                            ScanResultType(urlScanReportResponse = body)
                        )
                        findNavController().navigate(action)
                    }, 1500)
                } else {
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<UrlScanReportResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    fun getBase64(url: String): String {
        val base64Encoded =
            Base64.getUrlEncoder().withoutPadding().encodeToString(url.toByteArray(Charsets.UTF_8))
        return base64Encoded
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
        if (scheme == "content") {
            val cursor = requireActivity().contentResolver.query(this, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        return name.ifEmpty { lastPathSegment ?: "${System.currentTimeMillis()}" }
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

    private fun showAnalyzeFailure() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Internal Error")
            setMessage("Something went wrong, you can try again from history!")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Go History") { dialog, _ ->
                showToast(requireContext(), "Going to history")
                dialog.dismiss()
            }
        }.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}