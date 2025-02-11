package com.madinaappstudio.viruscheck.fragments

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.api.RetrofitService
import com.madinaappstudio.viruscheck.database.HistoryDao
import com.madinaappstudio.viruscheck.database.HistoryDatabase
import com.madinaappstudio.viruscheck.database.HistoryEntity
import com.madinaappstudio.viruscheck.databinding.DialogScanLoadingBinding
import com.madinaappstudio.viruscheck.databinding.FragmentScanBinding
import com.madinaappstudio.viruscheck.models.AnalysesModel
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import com.madinaappstudio.viruscheck.models.HistoryViewModel
import com.madinaappstudio.viruscheck.models.ScanResultType
import com.madinaappstudio.viruscheck.models.UrlScanReportResponse
import com.madinaappstudio.viruscheck.models.UrlScanResponse
import com.madinaappstudio.viruscheck.utils.ProgressLoading
import com.madinaappstudio.viruscheck.utils.getVirusApi
import com.madinaappstudio.viruscheck.utils.setLog
import com.madinaappstudio.viruscheck.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64
import kotlin.math.max

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var launcher: ActivityResultLauncher<String>? = null
    private lateinit var loadingDialog: LoadingDialog
    private val apiKey = getVirusApi()
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var handler: Handler
    private lateinit var pollingRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                loadingDialog.show()
                loadingDialog.setText("Uploading file...", "This won't take long, please wait...")
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
        handler = Handler(Looper.getMainLooper())

        val args = ScanFragmentArgs.fromBundle(requireArguments())
        val isFromHistory = args.isFromHistory

        if (isFromHistory != null && isFromHistory.size >= 2) {
            val type = isFromHistory[0]
            val scanId = isFromHistory[1]

            if (type == "file") {
                getFileReport(scanId, true)
            } else {
                getUrlReport(scanId, true)
            }
        }


        val historyDao: HistoryDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        val historyViewModelFactory = HistoryViewModel.HistoryViewModelFactory(historyDao)
        historyViewModel = viewModels<HistoryViewModel> { historyViewModelFactory }.value

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

        binding.mtScanToolbar.setOnMenuItemClickListener {
            val itemId = it.itemId

            when (itemId) {
                R.id.btnScanMenuHistory -> {
                    showHistoryDialog()
                    true
                }

                else -> {
                    false
                }
            }
        }

    }

    private fun showHistoryDialog() {
        findNavController().navigate(R.id.actionScanToHistory)
    }

    private fun isValidUrl(url: String): Boolean {
        val domainRegex = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return domainRegex.matches(url)
    }

    private fun scanFile(file: File) {
        val sha256 = getSHA256(file)
        val fileSize = Formatter.formatShortFileSize(requireContext(), file.length())

        historyViewModel.insertHistory(
            HistoryEntity(
                type = "file",
                name = file.name,
                fileSha256 = sha256,
                fileSize = fileSize,
                date = System.currentTimeMillis()
            )
        )

        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val call = RetrofitService.service.uploadFile(multipartBody, apiKey)

        call.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                p0: Call<FileUploadResponse>, p1: Response<FileUploadResponse>
            ) {
                val body = p1.body()
                if (body != null) {
                    runPolling(isFile = true, scanId = body.fileData.id, sha256 = sha256)
                } else {
                    loadingDialog.hide()
                    showToast(requireContext(), "Something went wrong! try again later")
                }
            }

            override fun onFailure(p0: Call<FileUploadResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun getFileReport(sha256: String, isHistory: Boolean = false) {
        val progressLoading = ProgressLoading(requireContext())
        if (isHistory) progressLoading.show()

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
                        if (isHistory) progressLoading.hide()
                        val action = ScanFragmentDirections.actionScanToScanResult(
                            ScanResultType(fileReportResponse = response)
                        )
                        findNavController().navigate(action)
                    }, 1500)

                } else {
                    if (isHistory) progressLoading.hide()
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<FileReportResponse>, p1: Throwable) {
                if (isHistory) progressLoading.hide()
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun scanUrl(url: String) {

        val urlBase64 = getBase64(url)
        historyViewModel.insertHistory(
            HistoryEntity(
                type = "url",
                name = url,
                urlBase64 = urlBase64,
                date = System.currentTimeMillis()
            )
        )

        loadingDialog.show()
        loadingDialog.setText("Sending request", "This won't take long, please wait...")

        val encodedUrl = "url=${URLEncoder.encode(url, "UTF-8")}"
        val requestBody =
            encodedUrl.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val call = RetrofitService.service.scanUrl(requestBody, apiKey)

        call.enqueue(object : Callback<UrlScanResponse> {
            override fun onResponse(p0: Call<UrlScanResponse>, p1: Response<UrlScanResponse>) {
                val body = p1.body()
                if (body != null) {
                    runPolling(isFile = false, scanId = body.urlData.id, urlBase64 = urlBase64)
                } else {
                    loadingDialog.hide()
                    showToast(requireContext(), "Something went wrong! try again later")
                }
            }

            override fun onFailure(p0: Call<UrlScanResponse>, p1: Throwable) {
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun getUrlReport(urlBase64: String, isHistory: Boolean = false) {
        val progressLoading = ProgressLoading(requireContext())
        if (isHistory) progressLoading.show()

        val call = RetrofitService.service.getUrlReport(urlBase64, apiKey)

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
                        if (isHistory) progressLoading.hide()
                        val action = ScanFragmentDirections.actionScanToScanResult(
                            ScanResultType(urlScanReportResponse = body)
                        )
                        findNavController().navigate(action)
                    }, 1500)
                } else {
                    if (isHistory) progressLoading.hide()
                    loadingDialog.hide()
                    showAnalyzeFailure()
                }
            }

            override fun onFailure(p0: Call<UrlScanReportResponse>, p1: Throwable) {
                if (isHistory) progressLoading.hide()
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage, Toast.LENGTH_LONG)
            }

        })
    }

    private fun getBase64(url: String): String {
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

    private fun getSHA256(file: File): String {
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
                findNavController().navigate(R.id.actionScanToHistory)
                dialog.dismiss()
            }
        }.show()

    }

    inner class LoadingDialog(private val context: Context) {

        private val binding = DialogScanLoadingBinding.inflate(LayoutInflater.from(context))

        private val builder = AlertDialog.Builder(context).create().apply {
            setView(binding.root)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setDimAmount(.8f)
        }

        fun show() {
            builder.show()
            builder.window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.80).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            binding.btDialogCancel.setOnClickListener {
//                cancelScanConfirmation()
                handler.removeCallbacks(pollingRunnable)
                hide()
            }
        }

        fun hide() {
            if (builder.isShowing) builder.hide()
        }

        fun setText(status: String, msg: String) {
            binding.tvDialogStatus.text = status
            binding.tvDialogMsg.text = msg
        }

        fun makeSuccessView(isFile: Boolean) {
            binding.btDialogCancel.visibility = View.GONE
            binding.pbDialogProgressC.visibility = View.GONE
            binding.ivDialogStatusImg.setImageResource(R.drawable.ic_success)
            binding.ivDialogStatusImg.visibility = View.VISIBLE
            binding.tvDialogStatus.text = "Scan Completed"
            binding.tvDialogStatus.setTypeface(null, Typeface.BOLD)
            binding.pbDialogProgressH.visibility = View.GONE
            if (isFile) {
                binding.tvDialogMsg.text = "File have been successfully analyzed"
            } else {
                binding.tvDialogMsg.text = "URL have been successfully analyzed"
            }
        }

        fun makeTimeoutView(){
            binding.btDialogCancel.visibility = View.GONE
            binding.pbDialogProgressC.visibility = View.GONE
            binding.ivDialogStatusImg.setImageResource(R.drawable.ic_timeout)
            binding.ivDialogStatusImg.visibility = View.VISIBLE
            binding.tvDialogStatus.text = "Request Timeout"
            binding.tvDialogStatus.setTypeface(null, Typeface.BOLD)
            binding.pbDialogProgressH.visibility = View.GONE
            binding.tvDialogMsg.text = "Request taking longer than expected. Try again from history"
        }

    }

    private fun cancelScanConfirmation() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Cancel Scan")
            setMessage("Are you sure want to cancel?")
            setPositiveButton("Yes") { dialog, _ ->
                handler.removeCallbacks(pollingRunnable)
                loadingDialog.hide()
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun runPolling(isFile: Boolean, scanId: String, sha256: String? = null, urlBase64: String? = null) {
        var attempt = 1
        val maxAttempt = 3
        var duration = 10000L
        pollingRunnable = Runnable {
            if (attempt >= maxAttempt) {
                loadingDialog.makeTimeoutView()
                CoroutineScope(Dispatchers.Main).launch{
                    delay(3000)
                    handler.removeCallbacks(pollingRunnable)
                    loadingDialog.hide()
                }
                return@Runnable
            }
            val analysisCall = RetrofitService.service.getAnalyses(scanId, apiKey)
            analysisCall.enqueue(object : Callback<AnalysesModel> {
                override fun onResponse(
                    call: Call<AnalysesModel>,
                    response: Response<AnalysesModel>
                ) {
                    val analysesModel = response.body()
                    if (analysesModel != null) {
                        val status = analysesModel.getData().attributes.status
                        if (status == "completed") {
                            handler.removeCallbacks(pollingRunnable)
                            if (isFile) getFileReport(sha256!!) else getUrlReport(urlBase64!!)
                        } else {
                            when(attempt){
                                1 -> {
                                    loadingDialog.setText(
                                        "Analysis Queued",
                                        "Your ${if (isFile) "file" else "URL"} has been added to the analysis queue. " +
                                                "This process may take a few seconds."
                                    )
                                }
                                else -> {
                                    loadingDialog.setText(
                                        "Analysis in Progress",
                                        "We are currently analyzing your ${if (isFile) "file" else "URL"}. " +
                                                "The process is underway and may take a moment. Hang tight!"
                                    )
                                }
                            }
                            attempt++
                            duration += 20000L
                            handler.postDelayed(pollingRunnable, duration)
                        }
                    } else {
                        loadingDialog.hide()
                        showToast(requireContext(), "Something went wrong! Try again later.")
                    }
                }
                override fun onFailure(call: Call<AnalysesModel>, t: Throwable) {
                    loadingDialog.hide()
                    showToast(requireContext(), t.localizedMessage)
                }
            })
        }
        handler.postDelayed(pollingRunnable, duration)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}