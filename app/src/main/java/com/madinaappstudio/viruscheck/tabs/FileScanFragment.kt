package com.madinaappstudio.viruscheck.tabs

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.api.RetrofitService
import com.madinaappstudio.viruscheck.databinding.FragmentFileScanBinding
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import com.madinaappstudio.viruscheck.utils.LoadingDialog
import com.madinaappstudio.viruscheck.utils.getVirusApi
import com.madinaappstudio.viruscheck.utils.showToast
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

class FileScanFragment : Fragment() {

    private var _binding: FragmentFileScanBinding? = null
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

        _binding = FragmentFileScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUploadUi()

        loadingDialog = LoadingDialog(requireContext())

        binding.btnFileScanUpload.setOnClickListener {
            launcher?.launch("*/*")
        }
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
                    loadingDialog.setText("Analyzing file...", "This may take long, please wait...")
                    val sha256 = getSHA256(file)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        getScanResult(sha256)
                    }, 1000)

                } else {
                    showUploadUi()
                    loadingDialog.hide()
                    showToast(requireContext(), "Failed to upload file")
                }
            }

            override fun onFailure(p0: Call<FileUploadResponse>, p1: Throwable) {
                showUploadUi()
                loadingDialog.hide()
                showToast(requireContext(), p1.localizedMessage)
            }

        })
    }

    private fun getScanResult(sha256: String) {

        val call = RetrofitService.service.getFileReport(sha256, apiKey)

        call.enqueue(object : Callback<FileReportResponse> {
            override fun onResponse(
                p0: Call<FileReportResponse>,
                p1: Response<FileReportResponse>
            ) {
                val response = p1.body()
                if (response != null) {
                    bindViewsData(response)
                } else {
                    showUploadUi()
                    loadingDialog.hide()
                    showToast(requireContext(), "Failed to analyze file")
                }
            }

            override fun onFailure(p0: Call<FileReportResponse>, p1: Throwable) {
                showUploadUi()
                loadingDialog.hide()
                showAnalyzeFailure()
            }

        })
    }

    private fun bindViewsData(response: FileReportResponse) {
        val attribute = response.data.attributes

        binding.tvFileScanFirstSub.text = formatDate(attribute.firstSubmissionDate)
        binding.tvFileScanLastSub.text = formatDate(attribute.lastSubmissionDate)
        binding.tvFileScanLastAnalysis.text = formatDate(attribute.lastAnalysisDate)

        setUndetected(attribute.lastAnalysisStats.undetected)
        setMalicious(attribute.lastAnalysisStats.malicious)
        setSuspicious(attribute.lastAnalysisStats.suspicious)
        setTimeout(attribute.lastAnalysisStats.timeout)

        showResultUi()
        loadingDialog.hide()
    }

    private fun setUndetected(count: Int) {
        val viewUndetected = binding.viewFileScanUndetected
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_undetected))
        viewUndetected.tvScanResultTitle.text = "Undetected"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_undetected)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setMalicious(count: Int) {
        val viewUndetected = binding.viewFileScanMalicious
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_malicious, null))
        viewUndetected.tvScanResultTitle.text = "Malicious"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_malicious)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setSuspicious(count: Int) {
        val viewUndetected = binding.viewFileScanSuspicious
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(
                requireContext().resources.getColor(
                    R.color.bg_suspicious,
                    null
                )
            )
        viewUndetected.tvScanResultTitle.text = "Suspicious"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.is_suspicious)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setTimeout(count: Int) {
        val viewUndetected = binding.viewFileScanTimeout
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_timeout, null))
        viewUndetected.tvScanResultTitle.text = "Timeout"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_timeout)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
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

    private fun showUploadUi() {
        binding.clFileScanUpload.visibility = View.VISIBLE
        binding.clFileScanResult.visibility = View.GONE
    }

    private fun showResultUi() {
        binding.clFileScanUpload.visibility = View.GONE
        binding.clFileScanResult.visibility = View.VISIBLE
    }

//    private fun changeView(){
//        if (binding.clFileScanUpload.isVisible){
//            binding.clFileScanUpload.visibility = View.GONE
//            binding.clFileScanResult.visibility = View.VISIBLE
//        } else {
//            binding.clFileScanUpload.visibility = View.VISIBLE
//            binding.clFileScanResult.visibility = View.GONE
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}