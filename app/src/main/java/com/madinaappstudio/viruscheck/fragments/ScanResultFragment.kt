package com.madinaappstudio.viruscheck.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.databinding.FragmentScanResultBinding
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.ScanResultType
import com.madinaappstudio.viruscheck.utils.LoadingDialog
import com.madinaappstudio.viruscheck.utils.setLog
import com.madinaappstudio.viruscheck.utils.showToast
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class ScanResultFragment : Fragment() {

    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!

    private val args: ScanResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanReport = args.scanReport

        binding.mtScanResultToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        bindViewsData(scanReport.fileReportResponse!!)

    }

    private fun bindViewsData(response: FileReportResponse) {
        val attribute = response.data.attributes

        binding.tvScanResultFirstSub.text = formatDate(attribute.firstSubmissionDate)
        binding.tvScanResultLastSub.text = formatDate(attribute.lastSubmissionDate)
        binding.tvScanResultLastAnalysis.text = formatDate(attribute.lastAnalysisDate)

        setUndetected(attribute.lastAnalysisStats.undetected)
        setMalicious(attribute.lastAnalysisStats.malicious)
        setSuspicious(attribute.lastAnalysisStats.suspicious)
        setTimeout(attribute.lastAnalysisStats.timeout)

    }

    private fun setUndetected(count: Int) {
        val viewUndetected = binding.viewScanResultUndetected
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_undetected, null))
        viewUndetected.tvScanResultTitle.text = "Undetected"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_undetected)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setMalicious(count: Int) {
        val viewUndetected = binding.viewScanResultMalicious
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_malicious, null))
        viewUndetected.tvScanResultTitle.text = "Malicious"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_malicious)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setSuspicious(count: Int) {
        val viewUndetected = binding.viewScanResultSuspicious
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
        val viewUndetected = binding.viewScanResultTimeout
        viewUndetected.cvScanResultMain
            .setCardBackgroundColor(requireContext().resources.getColor(R.color.bg_timeout, null))
        viewUndetected.tvScanResultTitle.text = "Timeout"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_timeout)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}